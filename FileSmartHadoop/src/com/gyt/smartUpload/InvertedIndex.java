package com.gyt.smartUpload;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @FileName: InvertedIndex.java
 * @Package: com.gyt.invertedIndex
 * @Author: Gu Yongtao
 * @Description: [类的描述]
 *
 * @Date: 2018年12月10日 下午4:30:51
 */

public class InvertedIndex {
	// 属性
	private static FileSplit inputSplit;
	
	/**
	 * Mapper静态内部类
	 * @author hadoop
	 *
	 */
	public static class InvertedMapper extends Mapper<LongWritable, Text, Text, Text> {

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			System.out.println("Starting Mapper: ");
			// 分割
			StringTokenizer stringTokenizer = new StringTokenizer(value.toString());
			
			// 获取切片来源文件名
			inputSplit = (FileSplit) context.getInputSplit();
			String fileName = inputSplit.getPath().getName().toString();
			// 判断输入文件是否是txt文件，不是则不执行map
			if (fileName.toLowerCase().endsWith(".txt")
					|| fileName.toLowerCase().endsWith(".xml")
					|| fileName.toLowerCase().endsWith(".html")
					|| fileName.toLowerCase().endsWith(".jsp")) {
				// 按 key=单词:文件名  value=1 输出，如 map:file1.txt
				// 只匹配英文字母
				// Pattern对象
//				Pattern r = Pattern.compile("([\\u4e00-\\u9fa5||A-Za-z_]+)");
				Pattern r = Pattern.compile("([A-Za-z\\u4e00-\\u9fa5A-Za-z||\\u4e00-\\u9fa5A-Za-z_\\u4e00-\\u9fa5]+)");
				while (stringTokenizer.hasMoreTokens()) {
					// Mather对象 
					Matcher m = r.matcher(stringTokenizer.nextToken());
					// 遍历
					while (m.find()) {
						String wordFile = m.group(1) + ":" + fileName;
						context.write(new Text(wordFile), new Text("1"));
					}
				}
			}
		}
	}
	
	/**
	 * Combiner静态内部类
	 * @author hadoop
	 *
	 */
	public static class InvertedCombiner extends Reducer<Text, Text, Text, Text> {

		@Override
		protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			System.out.println("Starting Combiner: ");
			// 将 key=word:file，value=1 进行计数
			int sum = 0;
			for (Text val : values) {
				sum += Integer.parseInt(val.toString());
			}
			// 将 key=word:file拆分
			int index = key.toString().indexOf(":");
			String word = key.toString().substring(0, index);
			// 略过三个及以下字符，原因：前端的搜索限定至少4个字符；减少存储、运行时间（虽然个人运行量级很小）
			if (word.length() >= 4) {
				String fileName = key.toString().substring(index+1);

				// 按 value=filename:counts 输出
				context.write(new Text(word), new Text(fileName + ":" + sum));
			}
		}
		
	}
	
	
	
	/**
	 * Reducer静态内部类
	 * @author hadoop
	 *
	 */
	public static class InvertedReducere extends Reducer<Text, Text, Text, Text> {

		@Override
		protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
//			System.out.println("Starting Reducer: ");
			
			// 遍历 values，用 ";" 组合
			String result = "\t";
			for (Text val : values) {
				result += val.toString() + ";";
			}
			context.write(key, new Text(result + "\r"));
		}
		
	}
	
	/**
	 * 
	 * @Title：putFile
	 * @Description: 上传文件到HDFS
	 * @Param: @param configuration
	 * @Return: void
	 * @Date: 2018年12月11日
	 */
	public static void putFile2HDFS(Configuration configuration) {
		// 访问数据库，得到文件地址，上传文件到HDFS
		System.out.println("----- Starting put file 2 HDFS -----");

		URI uri;
		try {
			uri = new URI("hdfs://localhost:9000");
			FileSystem fs = FileSystem.get(uri, configuration);
			// 查询所以的文件
			ResultSet resultSet = DAO.query();
			System.out.println("query over");
			// 遍历上传文件到HDFS
			while (resultSet.next()) {
				// 本地文件
				// 判断是否存在
				File file = new File(resultSet.getString("filepath"));
				if (!file.exists()) {	// 不存在则不上传
					continue;
				}
				Path src = new Path(resultSet.getString("filepath"));
				//HDFS存放位置
				Path dst = new Path("/data/fileSmartUpload");
				// 判断是否存在数据目录
				if (fs.exists(dst)) {
					fs.copyFromLocalFile(src, dst);
				} else {
					fs.mkdirs(dst);
					fs.copyFromLocalFile(src, dst);
				}
			}
		} catch (URISyntaxException | IOException | IllegalArgumentException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("----- Over put file 2 HDFS -----");
	}
	
	/**
	 * 
	 * @Title：invertedProcess
	 * @Description: 倒排索引
	 * @Param: @param args
	 * @Param: @param configuration
	 * @Return: void
	 * @Date: 2018年12月11日
	 */
	public static Boolean invertedProcess(String[] args, Configuration configuration) {
		// 执行倒排索引
		
		// 若存在输出目录，则先删除，解决每次运行任务需要先把输出目录删除
		/*
		 * 两种方式得到FileSystem
		 * 1. 使用new Path().getFileSystem()
		 * 2. 使用FileSystem.get()    该方法将报错，解决办法：将core-site.xml和hdfs-site.xml复制到 src目录
		 */
//		FileSystem fileSystem = FileSystem.get(configuration);
		FileSystem fileSystem;
		try {
			// 避免报错，先删除输出目录
			fileSystem = new Path(args[1]).getFileSystem(configuration);
			fileSystem.delete(new Path(args[1]), true);
			
			// Job
			Job job = Job.getInstance(configuration);
			job.setJarByClass(InvertedIndex.class);
			
			// 设置Class
			job.setMapperClass(InvertedMapper.class);
			job.setCombinerClass(InvertedCombiner.class);
			job.setReducerClass(InvertedReducere.class);
			// Map输出类型
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			// Reduce输出类型
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			// 输入输出路径
			FileInputFormat.addInputPath(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[1]));
			
			Boolean flag = job.waitForCompletion(true);
			if (flag) {
				System.out.println("Inverted success!");			
			} else {
				System.out.println("Inverted failed!");
			}
			return flag;
		} catch (IllegalArgumentException | IOException | ClassNotFoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public static void getFile2Local(String[] args, Configuration configuration) {

		try {
			FileSystem fileSystem = FileSystem.get(new URI("hdfs://localhost:9000"), configuration);
			// HDFS path
			Path src = new Path(args[1]);
			// local path
			// 判断是否存在，存在则删除
			File fileRoot = new File("D:\\soft2\\javaweb\\work_space\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\"
					+ "wtpwebapps\\FileSmartHadoop\\WEB-INF\\file\\fileSmartUpload");
			// 递归删除
			if (fileRoot.exists() && fileRoot.isDirectory()) {				
				deletFile(fileRoot);
				fileRoot.delete();
				System.out.println("Local file exists, already delete!");
			}
			Path dst = new Path("D:\\soft2\\javaweb\\work_space\\.metadata\\.plugins\\org.eclipse.wst.server.core\\"
					+ "tmp0\\wtpwebapps\\FileSmartHadoop\\WEB-INF\\file");
			fileSystem.copyToLocalFile(src, dst);
			fileSystem.close();
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("getFile 2 local Success!");
	}
	
	/**
	 * 
	 * @Title：deletFile
	 * @Description: 递归删除目录
	 * @Param: @param fileRoot
	 * @Return: void
	 * @Date: 2018年12月11日
	 */
	public static void deletFile(File fileRoot) {
		File[] files = fileRoot.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				deletFile(file);
			} else {
				file.delete();
			}
		}
	}
	
	public static void main(String[] args) {
		// 避免移植后忘记设置输入参数 hdfs://localhost:9000/data/fileSmartUpload  hdfs://localhost:9000/output/fileSmartUpload
		String[] inputOutputPath = {"hdfs://localhost:9000/data/fileSmartUpload",
				"hdfs://localhost:9000/output/fileSmartUpload"};
		
		
		// 驱动类
		Configuration configuration = new Configuration(true);
		
		// 上传文件到HDFS
		putFile2HDFS(configuration);
		
		// 倒排索引
		Boolean flag = invertedProcess(inputOutputPath, configuration);
		
		// 下载到本地
		if (flag) {
			getFile2Local(inputOutputPath, configuration);
		}

	}
}
