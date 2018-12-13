/**
 * 
 * @authors Your Name (you@example.org)
 * @date 2017-03-07 15:05:12
 * @version $Id$
 */

/* ------------------------------------------------------------------ */
/*
 * File /* ------------------------------------------------------------------
 */
$('input[type=button]').click(function(){
    if (window.File && window.FileReader && window.FileList && window.Blob) {
        console.log( 'browser ok' )
        $('#files').click();

    } else {
        showOverlay("Your current browser does not support file uploads\n\rPlease update or change a browser\n\rIt is recommended to use Google/firefox");
        return;
    }
})



function handleFileSelect(e){

    var files = e.target.files;
    var output = [];
    var maxSize = 10;	// 单个最大文件大小
    var xhr = new XMLHttpRequest();
    
	

    for (var i=0, f; f = files[i]; i++) {
        output.push( escape(f.name), "===", f.size, "===", f.lastModifiedDate.toLocaleDateString() )
        // File-size
        if ( parseInt(output[2]) >= maxSize*Math.pow(1024, 2) ) {

            console.log( parseInt(output[2]) );
            $('#files-info').val( '当前限制上传文件大小不超过'+'10'+'MB' );
            showOverlay('当前限制上传文件大小不超过'+'10'+'MB');

            files = null; output = [];
            $('#files').val("");
		
			
        } else {
            console.log('=======size-ok:'+parseInt(output[2]))
                 console.log($('#files').val())
	       	$("#form").submit();

            break
        }
        $('#files').val("");
        files = null; output = [];
    }
    $('#files-info').val( output.join('') );
}
document.getElementById('files').addEventListener('change', handleFileSelect, false);



/* ------------------------------------------------------------------ */
/*
 * Heart /* ------------------------------------------------------------------
 */
$('#h-animate').css({
    top: $(document).height() / 2.7 + 'px'
});
$('#h-animate').css({
    'margin-left': -$('#h-animate').width() / 2 + 'px'// startPage heart
});

// hover
$('#h-animate').mouseenter(function() {
    $('#h-animate').css({
        'margin': 'auto',
        'width': 'auto'
    });
    var offset = $(this).offset();
    $(this).html("&#10084;"); // Change heart
    // Get heart W/H
    var spanWidth = $(this).width();
    var spanHeight = $(this).height();

    var width = $(document).width() - spanWidth - 10; // -10 due to font
														// adjustment
    var height = $(document).height() - spanHeight - 10; // -10 due to font
															// adjustment
    // random W/H
    var x = (Math.random() * width);
    var y = (Math.random() * height);

  // Ensure new position will not accidently be under mouse again
    while (x >= (offset.left - spanWidth / 4) && x <= (offset.left + spanWidth)) {
        x = (Math.random() * width);
    }
    while (y >= (offset.top - spanHeight / 4) && y <= (offset.top + spanHeight)) {
        y = (Math.random() * height);
    }
    var randomR = parseInt(Math.random()*255);
    var randomG = parseInt(Math.random()*255);
    var randomB = parseInt(Math.random()*255);
    var randomO = Math.random() + 0.01
    var randomColor = "rgba("+randomR+","+randomG+","+randomB+","+randomO+")"
    $(this).css('left', x);
    $(this).css('top', y);
    $(this).css('color', randomColor)
});

// resizing position
$(window).resize(function() {
    $('#h-animate').css({
        top: $(document).height() / 3 + 'px'
    });
    $('#h-animate').css({
        'left': "50%"
    });
    $('#h-animate').css({
        'margin-left': -$('#h-animate').width() / 2 + 'px'
    });
});



/* ------------------------------------------------------------------ */
/*
 * BackGround 滚动播放背景图没有成功 /*
 * ------------------------------------------------------------------
 */
$('body').vegas({
    overlay: '../img/overlay.png',
    slides: [
        { src: '../img/bannerq7.jpg' },
        { src: '../img/bannerq6.jpg' },
        { src: '../img/bannerq5.jpg' },
        { src: '../img/bannerq4.jpg' },
        { src: '../img/bannerq3.jpg' },
        { src: '../img/bannerq2.jpg' },
        { src: '../img/bannerq1.jpg' },
    ],
})



/* ------------------------------------------------------------------ */
/*
 * Page-Loader /*
 * ------------------------------------------------------------------
 */
// Wait for window load
$(window).load(function() {
// Animate loader off screen
    $(".page-loader").fadeOut( "slow" );
    
    new WOW().init();

    new Clipboard($('button')[0]);
});



/* ------------------------------------------------------------------ */
/*
 * Shade /* ------------------------------------------------------------------
 */
function showOverlay(info, buttt) {
    info = info || "您有消息~";
    buttt = buttt || "OK";
    $("#ok").html( buttt );
    $('.text-info').html( info );
    $(".popup-window").fadeIn(500);
    // To prevent the failure CSS
    setTimeout("$('.stick').toggleClass(function() {return $(this).is('.open, .close') ? 'open close' : 'open'})",400)
};



    /* -------------------------------------------------------------- */
    /*
	 * Shade/1. window.animate /*
	 * --------------------------------------------------------------
	 */
    $(document).ready(function() {
        function hideOverlay() {
            $("#shade").animate( {"top": "100px"}, 50, function() {
                $(this).animate( {"top": "-999px"}, 200, function() {
                    $(".popup-window").fadeOut(300, function() {
                        $("#shade").css({"top":"0"});
                    });
                });
            })

        };
        $(".shade-container").click(function() {
            // To prevent the failure CSS
            $(".stick").toggleClass(function () {
                if($(this).is('.open, .close')){ hideOverlay()
                    return 'open close';
                };
                return $(this).is('.open, .close') ? 'open close' : 'open';
            });
            return false;
        });
        $(".popup-window").click( function(){
            // To prevent the failure CSS
            $(".stick").toggleClass(function () {
                if($(this).is('.open, .close')){ hideOverlay()
                    return 'open close';
                };
                return $(this).is('.open, .close') ? 'open close' : 'open';
            });
            return false;   
        });

    });

/* ------------------------------------------------------------------ */
/*
 * search /* ------------------------------------------------------------------
 */
$('.search-text-icon').click(function() {
    if( !$("#files-info").val() || $("#files-info").val().length < 4 ) {	// 加了个判断长度不得大于20的没有成功
        $("#files-info").focus().val("").attr("placeholder","内容为空或小于4个字符");
	
    } else if (!(/^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test($("#files-info").val()))) {
    	$("#files-info").focus().val("").attr("placeholder","内容包含特殊符号");
	} else {
    	window.location.href="http://localhost:8080/FileSmartHadoop/home-smart.jsp?page=1&kw=" + $("#files-info").val();
    };
})

