$(document).ready(function() {
	//使用
	$(function() {
	    $("img.lazy").lazyload();
	});
	$('input, textarea').placeholder();
	$('#brand .more').hover(function() {
		if(!$('#brand').hasClass('launch')) {
			$('#brand').addClass('launch');
		}
	});
	$('#brand').mouseleave(function() {
		if($('#brand').hasClass('launch')) {
			$('#brand').removeClass('launch');
		}
	});
	$('#brand .more .arrow-down').click(function() {
		$('#brand').hasClass('launch') && $('#brand').removeClass('launch');
	});
	$('.select .hover li').click(function() {
		var value = $(this).text();
		$(this).siblings('.active').each(function() {
			$(this).removeClass('active');
		})
		if (!$(this).hasClass('active')) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
		$(this).parents('.select').find('.sp').each(function() {
			$(this).text(value);
		});
	});
	$('.section:not("#other") .items a').click(function() {
		$(this).siblings('.active').each(function() {
			$(this).removeClass('active');
		})
		!$(this).hasClass('active') && $(this).addClass('active');
		if ($(this).parent().parent().attr('id') == 'brand') {
			$(this).prependTo($(this).parent());
		}
	});
	$('.section#other .items a').click(function() {
		$(this).siblings('.active').each(function() {
			$(this).removeClass('active');
		})
		!$(this).hasClass('active') && $(this).addClass('active');
	});
	$('.popup .fields .content .message .select').click(function() {
		if (!$(this).hasClass('active')) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
	});
	$('.you_may_like a').click(function() {
		$(this).siblings('.active').each(function() {
			$(this).removeClass('active');
		})
		!$(this).hasClass('active') && $(this).addClass('active');
	});
	$('.you_may_like_dot a').click(function() {
		$(this).siblings('.active').each(function() {
			$(this).removeClass('active');
		})
		!$(this).hasClass('active') && $(this).addClass('active');
	});
	$('#order .contents .order_item .choice_input').click(function() {
		if (!$(this).hasClass('active')) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
		
	});
	$('.order_confirm_list.valid ul li .choice_input').click(function() {
		if($(this).parents('.order_confirm_list ').attr('id') == 'submit_order_all') {
			return false;
		}
		$(this).parent('li').children('.choice_input.active').each(function() {
			$(this).removeClass('active');
		})
		if ($(this).parents('li.field_titles').length != 0) {
			!$(this).hasClass('active') ? $(this).parents('li.field_titles').parent('ul').find('.choice_input').each(function() {
				!$(this).hasClass('active') && $(this).addClass('active');
			}) : $(this).parents('li.field_titles').parent('ul').find('.choice_input.active').each(function() {
				$(this).removeClass('active');
			});
		} else {
			!$(this).hasClass('active') ? $(this).addClass('active') : $(this).removeClass('active');
		}
	});
	$("#submit_order_all .choice_input").click(function(){
		if (!$(this).hasClass('active')) {
			$(".order_confirm_list").find('.choice_input').each(function() {
				if (!$(this).hasClass('active')) {
					$(this).addClass('active');
				}
			})
		} else {
			$(".order_confirm_list").find('.choice_input').each(function() {
				if ($(this).hasClass('active')) {
					$(this).removeClass('active');
				}
			});
		}
	});
	$('.address_list .select_able ul li .radio').click(function() {
		$(this).parent('li').siblings('.active').each(function() {
			$(this).removeClass('active');
		})
		!$(this).parent('li').hasClass('active') && $(this).parent('li').addClass('active');
	});
	$('.address_list .select_able ul li .operate a.set_default').click(function() {
		$(this).parent('span').parent('div').parent('li').siblings('.default').each(function() {
			$(this).removeClass('default');
		})
		!$(this).parent('span').parent('div').parent('li').hasClass('default') && $(this).parent('span').parent('div').parent('li').addClass('default');
	});
	$('.address_list .title span.more').click(function() {
		if(!$(this).hasClass('less')) {
			!$(this).addClass('less');
			$(this).text('收起地址');
			if ($(this).parent('.title').siblings('.select_able').children('ul').hasClass('less')) {
				$(this).parent('.title').siblings('.select_able').children('ul').removeClass('less');
			}
		} else {
			$(this).removeClass('less');
			$(this).text('更多地址');
			if (!$(this).parent('.title').siblings('.select_able').children('ul').hasClass('less')) {
				$(this).parent('.title').siblings('.select_able').children('ul').addClass('less');
			}
		}
	});
	$('.add_tickets .order_item .tiket_type').click(function() {
		$(this).siblings().each(function() {
			$(this).hasClass('selected') && $(this).removeClass('selected');
		});
		!$(this).hasClass('selected') && $(this).addClass('selected');
	});
	$('.pay_list .select_able li .method').click(function() {
		$(this).parent('li').siblings().each(function() {
			$(this).children('.method').hasClass('selected') && $(this).children('.method').removeClass('selected');
		});
		!$(this).hasClass('selected') && $(this).addClass('selected');
		var method = $(this).data('method');
		$(this).parents('.select_able').siblings('.pay_sheet').children('ul').children('li').each(function() {
			if ($(this).data('method') == method) {
				if (!$(this).hasClass('active')) {
					$(this).addClass('active');
				}
			} else {
				if ($(this).hasClass('active')) {
					$(this).removeClass('active');
				}
			}
		});
	});
	$('#address_input .close').click(function() {
		$(this).siblings('.focus').children('input').val('');
	});
	$('.popup.add_tickets #address .spec_address#address_input .close').click(function() {
		!$(this).parents('.popup').hasClass('disn') && $(this).parents('.popup').addClass('disn');
		!$('.mask').hasClass('disn') && !$('.mask').addClass('disn');
	});
	$('.transfer_list .select_able li .method').click(function() {
		$(this).parent('li').siblings().each(function() {
			$(this).children('.method').hasClass('selected') && $(this).children('.method').removeClass('selected');
		});
		!$(this).hasClass('selected') && $(this).addClass('selected');
	});
	$('.pop .fields .content .message .select').click(function() {
		if (!$(this).hasClass('active')) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
	});
	$('#address #phone .select').click(function() {
		if (!$(this).hasClass('active')) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
	});
	$('.product_show .services span.like').click(function() {
		if (!$(this).hasClass('active')) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
	});
	$('.popup .head .close').click(function() {
		!$(this).parents('.popup').hasClass('disn') && $(this).parents('.popup').addClass('disn');
		!$('.mask').hasClass('disn') && !$('.mask').addClass('disn');
	});
	$('.popup .shopping .cancel, .popup .shopping .save').click(function() {
		!$(this).parents('.popup').hasClass('disn') && $(this).parents('.popup').addClass('disn');
		!$('.mask').hasClass('disn') && !$('.mask').addClass('disn');

		if ('save_ticket_content_button' == $(this).attr('id')) {
			var tikets_content = $('#tikets_content .input .magic.selected span').text();
			var tikets_content_title = $('.tiket_corp_group .input_area.selected:not(".disn"):not("editor") input').val();
			if ('明细' == tikets_content && 0 < tikets_content_title.length) {
				$('#tiket_content_summary #tiket_detail_name_change_area .magic.button').click();
				$('#tiket_content_summary #tiket_detail_name_change_area #tiket_detail_name').text(tikets_content_title);
			} else {
				$('#tiket_content_summary #tiket_detail_name_change_area_no_ticket .magic.button span').click();
				$('#tiket_content_summary #tiket_detail_name_change_area #tiket_detail_name').text('');
			}

			if (0 == $('#tiket_content_summary #tiket_detail_name_change_area #tiket_detail_name').text().length) {
				$('#tiket_content_summary #tiket_detail_name_change_area #tiket_detail_name').addClass('disn');
				!$('#tiket_detail_name_change_area .change_tikets_button').hasClass('disn') && 
				$('#tiket_detail_name_change_area .change_tikets_button').addClass('disn');
			} else {
				$('#tiket_content_summary #tiket_detail_name_change_area #tiket_detail_name').removeClass('disn');
				$('#tiket_detail_name_change_area .change_tikets_button').hasClass('disn') && 
				$('#tiket_detail_name_change_area .change_tikets_button').removeClass('disn');
			}
		}
	});
	$('.contents .product_shop .count .operate div').click(function() {
		var $shopCount = $('.contents .product_shop .count .input input');
		var shoppingCount = $(this).hasClass('plus') ? (parseInt($shopCount.val()) + 1) : (parseInt($shopCount.val()) - 1);
		shoppingCount < 2 && (shoppingCount = 2);
		$shopCount.val(shoppingCount);
	});
	$('.order_confirm_list.valid ul li .num .caculate div').click(function() {
		var $shopCount = $(this).parent('.caculate').find('input');
		var shoppingCount = $(this).hasClass('plus') ? (parseInt($shopCount.val()) + 1) : (parseInt($shopCount.val()) - 1);
		shoppingCount < 2 && (shoppingCount = 2);
		$shopCount.val(shoppingCount);
	});
	
	var itemsChange = function() {
		// Instantiate EasyZoom instances
		function dotMove($ele, from, to) {
			var idx = parseInt(to, 10);
			bx.goToSlide(idx);
		}
		// var like_bx = $('.recoment_list #list_result .items ul.slide_ul').bxSlider({
		// 	pager:true,
		// 	pagerSelector:'.you_may_like_dot a',
		// 	nextText: '',
		// 	prevText: ''
		// });
		if ($('.recoment_list #list_result .items ul.slide_ul').length == 0) {
			return ;
		}
		$('.recoment_list #list_result .items.disn ul.slide_ul').stop();
		var like_cx = $('.recoment_list #list_result .items:not(.disn) ul.slide_ul').bxSlider({
			mode: 'horizontal',
			maxSlides: 16,
			minSlides: 5,
			moveSlides: 5,
			slideWidth: 202,
			slideMargin: 47,
			pager: true,
			pagerCustom:'.you_may_like_dot',
			onSlideBefore: like_bxMove,
			infiniteLoop: true,
			pagerActiveClass:'active',
			auto: true,
			nextText: '',
			prevText: ''
		});
		function like_bxMove($ele, from, to) {
			var idx = parseInt(to, 10); 
			$('.you_may_like_dot a').each(function() {
				$(this).hasClass('active') && $(this).removeClass('active');
			})
			$('.you_may_like_dot a').eq(idx).addClass('active');
		}
	}


	itemsChange();

	if ($('.bxslider').length != 0) {
		var bx = $('.bxslider').bxSlider({
			pagerCustom: '.thumbs_slider .wrapper',
			nextText: '',
			prevText: ''
		});
		var cx = $('.thumbs_slider .wrapper').bxSlider({
			mode: 'horizontal',
			maxSlides: 100,
			minSlides: 5,
			moveSlides: 1,
			slideWidth: 94,
			slideMargin: 0,
			pager: false,
			onSlideBefore: bxMove,
			nextSelector: '.product_show .img_slider .turn.right',
			prevSelector: '.product_show .img_slider .turn.left',
			nextText: '',
			prevText: ''
		});
		function bxMove($ele, from, to) {
			var idx = parseInt(to, 10);
			bx.goToSlide(idx);
		}
	}
	$(".add_new_address").click(function() {
		$('.mask').hasClass('disn') && $('.mask').removeClass('disn');
		$('#new_address_pop').hasClass('disn') && $('#new_address_pop').removeClass('disn');
	})
	$(".change_tikets_button").click(function() {
		$('.mask').hasClass('disn') && $('.mask').removeClass('disn');
		$('#add_tickets_pop').hasClass('disn') && $('#add_tickets_pop').removeClass('disn');
	})
	$("#pay_offline_tips").click(function() {
		$('.mask').hasClass('disn') && $('.mask').removeClass('disn');
		$('#pay_offline_pop').hasClass('disn') && $('#pay_offline_pop').removeClass('disn');
	})
	$(".cart_delete_product").click(function() {
		$('.mask').hasClass('disn') && $('.mask').removeClass('disn');
		$('#cart_delete_pop').hasClass('disn') && $('#cart_delete_pop').removeClass('disn');
	})
	$(".cart_favor_product").click(function() {
		$('.mask').hasClass('disn') && $('.mask').removeClass('disn');
		$('#cart_favor_pop').hasClass('disn') && $('#cart_favor_pop').removeClass('disn');
	})
	$(".list_sort li.m_sort").click(function() {
		$(this).siblings('.m_sort').each(function(){
			$(this).hasClass('active') && $(this).removeClass('active');
		});
		!$(this).hasClass('active') && $(this).addClass('active');
		if (!$(this).hasClass('up')) {
			$(this).addClass('up');
		} else {
			$(this).removeClass('up');
		}
	});
	
	// function ruleStart(abs_x) {
	// 	abs_x = parseFloat(abs_x);
	// 	if (abs_x < 16.5) {
	// 		return (100 / 16.5) * abs_x;
	// 	}
	// 	if (abs_x >= 16.5 && abs_x <= 472.5) {
	// 		return 100 + ((3000 - 100) / (472.5 -16.5) )	* (abs_x - 16.5);
	// 	}
	// 	if (abs_x > 472.5 && abs_x <= 685.5) {
	// 		return 3000 + ( (8000 - 3000) / (685.5 - 472.5))	* (abs_x - 472.5);
	// 	}
	// 	return '8000+';
	// }
	// var move=false; 
	// $("#price .items .bar .validate .start").mousedown(function(ev){ 
	// 	move = true;
	// }); 
	// $(document).mousemove(function(ev){ 
	// 	if(move){ 
	// 		var init_left = $("#price .items .bar").offset().left;
	// 		var init_start_left = $("#price .items .start").offset().left;

	// 		var abs_x = ev.pageX - init_left;
	// 		abs_x = abs_x < 0 ? 0 : (abs_x > 712 ? 712 : abs_x);

	// 		if ($("#price .items .bar .validate .end").offset().left - init_left < 712 || (init_start_left - ev.pageX > 0)) {
	// 			var realwith = $("#price .items .bar .validate").width();
	// 			realwith = realwith + ($("#price .items .bar .validate").offset().left - init_left - abs_x);
	// 			if (realwith >= 30) {
	// 				$("#price .items .bar .validate .start").children('i').text(parseInt(ruleStart(abs_x)));
	// 				$("#price .items .bar .validate").css({'marginLeft': abs_x + 'px', 'width' : realwith});
	// 				console.log(ruleStart(abs_x));
	// 			}
	// 		}
	// 	}
	// }).mouseup(function(){ 
	// 	move=false; 
	// });

	// var endmove=false; 
	// $("#price .items .bar .validate .end").mousedown(function(ev){ 
	// 	endmove = true;
	// }); 
	// $(document).mousemove(function(ev){ 
	// 	if(endmove){ 
	// 		var abs_x = ev.pageX - $("#price .items .bar").offset().left;
	// 		if (abs_x <= 712) {
	// 			var width = ev.pageX - $("#price .items .bar .validate").offset().left;
	// 			width = width < 30 ? 30 : (width > 712 ? 712 : width);
	// 			var show_text = parseInt(ruleStart($("#price .items .bar .validate .end").offset().left - $("#price .items .bar").offset().left));
	// 			$("#price .items .bar .validate .end").children('i').text(show_text >= 8000 ? '8000+' : show_text);
	// 			$("#price .items .bar .validate").css('width', width + 'px');
	// 		}

	// 	}
	// }).mouseup(function(){ 
	// 	endmove=false; 
	// });




	// function angleRuleStart(abs_x) {
	// 	abs_x = parseFloat(abs_x);
	// 	var show_num = 12;
	// 	if (abs_x < 9) {
	// 		show_num = (8 / 9) * abs_x;
	// 	}
	// 	if (abs_x >= 9 && abs_x <= 369) {
	// 		show_num = 8 + ((11 - 8) / (369 - 8) )	* (abs_x - 9);
	// 	}
	// 	if (abs_x > 369 && abs_x <= 429) {
	// 		show_num = 11 + ((12 - 11) / (429 - 369) )	* (abs_x - 369);
	// 	}
	// 	if (show_num % 0.5 == 0) {
	// 		return show_num;
	// 	}
	// 	return show_num - show_num % 0.5;
	// }
	// var anglemove=false; 
	// $("#angle .items .bar .validate .start").mousedown(function(ev){ 
	// 	anglemove = true;
	// }); 
	// $(document).mousemove(function(ev){ 
	// 	if(anglemove){ 
	// 		var init_left = $("#angle .items .bar").offset().left;
	// 		var init_start_left = $("#angle .items .start").offset().left;

	// 		var abs_x = ev.pageX - init_left;
	// 		abs_x = abs_x < 0 ? 0 : (abs_x > 464 ? 464 : abs_x);

	// 		if ($("#angle .items .bar .validate .end").offset().left - init_left < 464 || (init_start_left - ev.pageX > 0)) {
	// 			var realwith = $("#angle .items .bar .validate").width();
	// 			realwith = realwith + ($("#angle .items .bar .validate").offset().left - init_left - abs_x);
	// 			if (realwith >= 30) {
	// 				$("#angle .items .bar .validate .start").children('i').data('num', parseFloat(angleRuleStart(abs_x)).toFixed(1));
	// 				$("#angle .items .bar .validate").css({'marginLeft': abs_x + 'px', 'width' : realwith});
	// 			}
	// 		}
	// 	}
	// }).mouseup(function(){ 
	// 	anglemove=false; 
	// });

	// var angleendmove=false; 
	// $("#angle .items .bar .validate .end").mousedown(function(ev){ 
	// 	angleendmove = true;
	// }); 
	// $(document).mousemove(function(ev){ 
	// 	if(angleendmove){ 
	// 		var abs_x = ev.pageX - $("#angle .items .bar").offset().left;
	// 		if (abs_x <= 464) {
	// 			var width = ev.pageX - $("#angle .items .bar .validate").offset().left;
	// 			width = width < 30 ? 30 : (width > 464 ? 464 : width);
	// 			var show_text = parseFloat(angleRuleStart(($("#angle .items .bar .validate .end").offset().left + 14.5) - $("#angle .items .bar").offset().left)).toFixed(1);
	// 			$("#angle .items .bar .validate .end").children('i').data('num', show_text);
	// 			$("#angle .items .bar .validate").css('width', width + 'px');
	// 		}

	// 	}
	// }).mouseup(function(){ 
	// 	angleendmove=false; 
	// });


	$("img").lazyload();
	$(".index_filter").click(function() {
		$(".list").hasClass('disn') && $(".list").removeClass('disn');
		!$(".slogan").hasClass('disn') && $(".slogan").addClass('disn');
	});
	$(".find_count .w #reset").click(function() {
		!$(".list").hasClass('disn') && $(".list").addClass('disn');
		$(".slogan").hasClass('disn') && $(".slogan").removeClass('disn');
	});

	$(window).scroll(function() {
		var st = $(window).scrollTop();
		if (st >= 0) {
			$('.tools.fixed').css({'position':'fixed', 'top':'0px'});
			$('.header.fixed').css({'position':'fixed', 'top':'41px'});
			$('.menu.fixed').css({'position':'fixed', 'top':'130px'});
			$('.fixed_placeholder').hasClass('disn') && $('.fixed_placeholder').removeClass('disn');
			$('.fixed_placeholder_order_list').hasClass('disn') && $('.fixed_placeholder_order_list').removeClass('disn');
		} else {
			$('.menu.fixed').css({'position':'relative'});
			$('.tools.fixed').css({'position':'relative'});
			$('.header.fixed').css({'position':'relative'});
		}
	});
	$('.load_more').click(function() {
		$(this).siblings('.items').find('ul').css({'max-height':'none'});
		$(this).siblings('.pagination').hasClass('disn') && $(this).siblings('.pagination').removeClass('disn');
		!$(this).hasClass('disn') && $(this).addClass('disn');
	});
	$('.you_may_like a').click(function() {
		var eq = $(this).index();
		$('#list_result .items').each(function() {
			!$(this).hasClass('disn') && $(this).addClass('disn');
		})
		// $('.you_may_like_dot a').each(function() {
		// 	$(this).hasClass('active') && $(this).removeClass('active');
		// })
		$('#list_result .items').eq(eq).hasClass('disn') && $('#list_result .items').eq(eq).removeClass('disn');
		// !$('.you_may_like_dot a').eq(eq).hasClass('active') && $('.you_may_like_dot a').eq(eq).addClass('active');
		itemsChange();
	});

	$('#price .items .bar input.range-slider').jRange({
		from: 0,//起点
		to: 8000,
		step: 100,//步长
		scale: [0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, '8000+'],//刻度线
		format: '￥%s',
		width: 712,
		showLabels: true,
		isRange : true
	});

	$('.yihaomu#angle .items .bar input.range-slider').jRange({
		from: 8,//起点
		to: 12,
		step: 0.5,//步长
		scale: ['...8',8.5,9,9.5,10,10.5,11,11.5,'12...'],//刻度线
		format: '￥%s',
		width: 464,
		showLabels: true,
		isRange : true
	});

	$('.tiemugan#angle .items .bar input.range-slider').jRange({
		from: 16,//起点
		to: 32,
		step: 1,//步长
		scale: [16, '', 18, '', 20, '', 22, '', 24, '', 26, '', 28, '', 30, '', '32...'],//刻度线
		format: '%s',
		width: 464,
		showLabels: true,
		isRange : true
	});

	$('.tuigan#angle .items .bar input.range-slider').jRange({
		from: 32,//起点
		to: 49,
		step: 1,//步长
		scale: [32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49],//刻度线
		format: '%s',
		width: 488,
		showLabels: true,
		isRange : true
	});

	$('.qiudaomu#angle .items .bar input.range-slider').jRange({
		from: 13,//起点
		to: 21,
		step: 1,//步长
		scale: [13, 14, 15, 16, 17, 18, 19, 20, 21],//刻度线
		format: '%s',
		width: 464,
		showLabels: true,
		isRange : true
	});

	$('#waqigan_angle .items .bar input.range-slider').jRange({
		from: 46,//起点
		to: 64,
		step: 2,//步长
		scale: [46, 48, 50, 52, 54, 56, 58, 60, 62, 64],//刻度线
		format: '￥%s',
		width: 312,
		showLabels: true,
		isRange : true
	});

	$('#waqigan_angle_rebound .items .bar input.range-slider').jRange({
		from: 4,//起点
		to: 12,
		step: 2,//步长
		scale: [4, 6, 8, 10, 12],//刻度线
		format: '￥%s',
		width: 240,
		showLabels: true,
		isRange : true
	});

	$('#bar_count .items .bar input.range-slider').jRange({
		from: 2,//起点
		to: 12,
		step: 1,//步长
		scale: ['#2',3,4,5,6,7,8,9,'9P','9A','9S'],//刻度线
		format: '￥%s',
		width: 314,
		showLabels: true,
		isRange : true
	});

	

	var mokeHeight = function(el) {
		el.each(function(){
			$(this).css({
				'height': $(this).siblings('.count').height() + 'px',
				'line-height': $(this).siblings('.count').height()+ 'px'
			});
		});		
	}
	mokeHeight($('.order_item .order_detail .receive'));
	mokeHeight($('.order_item .order_detail .status'));
	mokeHeight($('.order_item .order_detail .operate'));

	$('#order .contents .order_detail .price .table_cell').each(function() {
		$(this).css('marginTop', (($(this).parent('.table').parent('.price').parent('.order_detail').height() - $(this).height()) / 2) + 'px');
	});

	$('#order .contents .order_detail .operate .table').each(function() {
		$(this).css('marginTop', (($(this).parent('.operate').height() - $(this).height()) / 2) + 'px');
	});

	$('.upload_area_tips').click(function() {
		$('.upload_area input[type="file"]').click();
	});


	function getPath(file) {
		var url=null 
	    if(window.createObjectURL!=undefined){ // basic  
	        url=window.createObjectURL(file)  
	    }else if(window.URL!=undefined){ // mozilla(firefox)  
	        url=window.URL.createObjectURL(file)  
	    } else if(window.webkitURL!=undefined){ // webkit or chrome  
	        url=window.webkitURL.createObjectURL(file)  
	    }  
	    return url
	}

	$('.upload_area input[type="file"]').change(function() {
		var uri = getPath(this.files[0]);

		//验证上传文件格式是否正确   
        var pos = $(this).val().lastIndexOf(".");   
        var lastname = $(this).val().substring(pos, $(this).val().length)   
        if (
        	(lastname.toLowerCase() != ".jpg" ) &&
        	(lastname.toLowerCase() != ".gif" ) &&
        	(lastname.toLowerCase() != ".jpeg" ) &&
        	(lastname.toLowerCase() != ".bmp" )
        	) {   
            alert("您上传的文件类型为" + lastname + "，必须为jpg, gif, jpeg, bmp 类型");   
            return false;   
        }   

        //验证上传文件是否超出了大小 
        var image = new Image();  
        image.src = uri;  
        if (this.files[0].size / 1024 > 4096) {   
            alert("您上传的文件大小超出了4m限制！");   
            return false;   
		} 

		if ($('.upload_area_tips').parent('.upload_area').hasClass('disn')) {
			$('.upload_area_tips').parent('.upload_area').removeClass('disn')
		}
		if ($('.upload_area_tips').siblings('div').has('img')) {
			$('.upload_area_tips').siblings('div').html(image);
		} else {
			$('.upload_area_tips').siblings('div').html(image);
		}
	})

	$('.mask').click(function() {
		$('.popup .head .close').click();
	});

	$('#change_name').click(function() {
		if ($('.change_name').hasClass('disabled')) {
			$('.change_name').removeClass('disabled')
		}
		$('.change_name').removeClass('disabled')
		$('.change_name').attr('disabled', false)
		$('#input_name_tips_parent').addClass('mb20');
		
	});
	var checkStr = function(str){ 
		// [\u4E00-\uFA29]|[\uE7C7-\uE7F3]汉字编码范围 
		var re1 = new RegExp("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-zA-Z0-9])*$"); 
		if (!re1.test(str)){ 
			return false; 
		}
		return true; 
	};

	$('input.change_name').blur(function() {
		if (checkStr($(this).val())) {
			$(this).parent('.change_name').addClass('validate')
			$('#input_name_tips_parent').hasClass('mb20') && $('#input_name_tips_parent').removeClass('mb20');
		}
	});





	// 发票新增
	var editClick = function() {
		$('#address #address_input .tiket_corp_group').find('.input_area:not(.disn#add_new_tiket_button_input)').removeClass('selected');
		$('#address #address_input .tiket_corp_group').find('.input_area:not(.disn#add_new_tiket_button_input)').removeClass('editor');

		if ($(this).text() == '编辑') {
			$(this).parent('.edit_delete').siblings('.focus').children('input').attr('disabled', false);
			$(this).parent('.edit_delete').parent('.input').parent('.input_area').addClass('selected');
			$(this).parent('.edit_delete').parent('.input').parent('.input_area').addClass('editor');
			$(this).text('保存');
		} else {
			$(this).parent('.edit_delete').parent('.input').parent('.input_area').addClass('selected');
			$(this).parent('.edit_delete').siblings('.focus').children('input').attr('disabled', true);
			$(this).text('编辑');
			if (!$(this).hasClass('mr20')) {
				$(this).addClass('mr20');
			}
		}

		if ('add_new_tiket_button_input' == $(this).parent('.edit_delete').parent('.input').parent('.input_area').attr('id')) {
			$(this).parent('.edit_delete').parent('.input').parent('.input_area').attr('id', false); 
			if ($('#add_new_tiket_button_a').parent('.message').parent('.order_item').hasClass('disn')) {
				$('#add_new_tiket_button_a').parent('.message').parent('.order_item').removeClass('disn');
			}
		}

		$(this).siblings('.delete.disn').removeClass('disn')
	};
	$('#address #address_input .tiket_corp_group .input .edit_delete .edit').click(editClick);

	var inputClick = function() {
		if (0 < $(this).find('input[disabled]').length) {
			$('#address #address_input .tiket_corp_group').find('.input_area:not(.disn#add_new_tiket_button_input)').removeClass('selected');
			$('#address #address_input .tiket_corp_group').find('.input_area:not(.disn#add_new_tiket_button_input)').removeClass('editor');
			$(this).addClass('selected');
		}
	};
	$('#address #address_input .tiket_corp_group .input_area').click(inputClick)

	var deleteClick = function() {
		if (!$('.popup.add_tickets').hasClass('disn')) {
			$('.popup.add_tickets').addClass('disn');
		}

		$('#delete_tiket_now_pop').data('delete', $(this).parents('.input_area').index());

		if ($('#delete_tiket_now_pop').hasClass('disn')) {
			$('#delete_tiket_now_pop').removeClass('disn');
		}
	};
	$('#address #address_input .tiket_corp_group .input .edit_delete .delete').click(deleteClick);

	var saveLick = function() {
		$('#address #address_input .tiket_corp_group .input_area').eq($('#delete_tiket_now_pop').data('delete')).remove();
		
		if (!$('#delete_tiket_now_pop').hasClass('disn')) {
			$('#delete_tiket_now_pop').addClass('disn');
		}
		
		if ($('.popup.add_tickets').hasClass('disn')) {
			$('.popup.add_tickets').removeClass('disn');
		}

		if ($('.mask').hasClass('disn')) {
			$('.mask').removeClass('disn');
		}
	};
	$('#delete_tiket_now_pop .shopping .save').click(saveLick);

	$('#add_new_tiket_button_a').click(function() {
		if (!$(this).parent('.message').parent('.order_item').hasClass('disn')) {
			$(this).parent('.message').parent('.order_item').addClass('disn');
		}
		$('#address #address_input .tiket_corp_group').find('.input_area:not(.disn#add_new_tiket_button_input) input').attr('disabled', true);
		$('#address #address_input .tiket_corp_group').find('.input_area:not(.disn#add_new_tiket_button_input)').removeClass('selected');
		$('#address #address_input .tiket_corp_group').find('.input_area:not(.disn#add_new_tiket_button_input)').removeClass('editor');
		if ($('#address #address_input .tiket_corp_group #add_new_tiket_button_input').hasClass('disn')) {
			var $cloneSh = $('#address #address_input .tiket_corp_group .disn#add_new_tiket_button_input').clone();
			$('#address #address_input .tiket_corp_group #add_new_tiket_button_input').removeClass('disn');
			$('.tiket_corp_group').scrollTop(10000);
			$('.tiket_corp_group').append($cloneSh);
			$cloneSh.find('.edit_delete .delete').click(deleteClick);
			$cloneSh.click(inputClick);
			$cloneSh.find('.input .edit_delete .edit').click(editClick);
		}
	})
 
	$('#order .contents .order_item').mouseleave(function() {
		if($(this).find('.order_detail .operate span').text() == '退货退款') {

			$(this).find('.order_detail .operate').html('<a href="javascript:;" class="co-blue">退货退款</a>');

			if ($(this).hasClass('bo-blue')) {
				$(this).removeClass('bo-blue');
			}

			if ($(this).find('.order_detail .operate').hasClass('cof-a')) {
				$(this).find('.order_detail .operate').removeClass('cof-a');
			}
			if ($(this).find('.order_detail .operate').hasClass('f16')) {
				$(this).find('.order_detail .operate').removeClass('f16');
			}
		}
	});

	$('#order .contents .order_detail .operate').click(function() {
		if($(this).find('a').text() == '退货退款' && !$(this).parents('.order_item').hasClass('bo-blue')) {
			$(this).parents('.order_item').addClass('bo-blue');
			$(this).html('<span class="bg-blue button">退货退款</span>');
			if (!$(this).hasClass('cof-a')) {
				$(this).addClass('cof-a');
			}
			if (!$(this).hasClass('f16')) {
				$(this).addClass('f16');
			}
			
		}
	});

	if ($('#order .w .order_menu ul').height() < $('#order .contents').parent('.w').height()) {
		$('.order_menu').css('height', $('#order .contents').parent('.w').height());
	}


	if ($(window).scrollTop() > 100) {
		$(".gotop").fadeIn(100);
	}

	$(window).scroll(
		function () {
			if ($(window).scrollTop() > 100) {
				$(".gotop").fadeIn(100);
			} else {
				$(".gotop").fadeOut(100);
			}
		}
	);

	$(".gotop").click(function () {
		$('body,html').animate({ scrollTop: 0 }, 200);
		return false;
	});
});