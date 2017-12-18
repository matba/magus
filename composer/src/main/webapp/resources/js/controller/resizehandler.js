function resizeHandler() {
	$(".maincontainer").css("height", $(window).height() - stlNavBarTopSize);
	$("#bottomtabs").css(
			"height",
			$(".console-container").height() - toolbarHeight
					- stlConsoleFixedSize);
	$("#flowtabs").css("height",
			$(".flow-container").height() - stlFlowFixedSize);
	$("#fmtabs").css("height", $(".fm-container").height() - stlFMFixedSize);

	$("#contextModeltab").css("height",
			$(".fm-container").height() - stlFMFixedSize);
	$("#contextModeltab").css("width", $(".fm-container").width());

	$("#serviceAnnotationtab").css("height",
			$(".fm-container").height() - stlFMFixedSize);
	$("#serviceAnnotationtab").css("width", $(".fm-container").width());

	$("#fmedittab").css("height",
			$(".flow-container").height() - stlFMFixedSize);
	$("#fmedittab").css("width", $(".flow-container").width());
	$("#fmconftab").css("height", $(".fm-container").height() - stlFMFixedSize);
	$("#fmconftab").css("width", $(".fm-container").width());

	$("#adaptationRequirementsTab").css("height",
			$(".fm-container").height() - stlFMFixedSize);
	$("#adaptationRequirementsTab").css("width", $(".fm-container").width());

	$("#adaptRequirementsContainer").css("height",
			Math.floor(($(".fm-container").height() - stlFMFixedSize) * 0.7));
	$("#adaptRequirementsContainer").css("width", $(".fm-container").width());

	$("#nonfunctionalConfigurationContainer").css("height",
			Math.floor(($(".fm-container").height() - stlFMFixedSize) * 0.3));
	$("#nonfunctionalConfigurationContainer").css("width",
			$(".fm-container").width());

	$("#contextStateModelTab").css("height",
			$(".fm-container").height() - stlFMFixedSize);
	$("#contextStateModelTab").css("width", $(".fm-container").width());

	$("#bpelCodeTab").css("height",
			$(".flow-container").height() - stlFMFixedSize);
	$("#bpelCodeTab").css("width", $(".flow-container").width());

	bpelCodeMirror.setSize($(".flow-container").width() + "px", $(
			".flow-container").height()
			- toolbarHeight - stlFMFixedSize + "px");
	$("#workflowtab").css("height",
			$(".flow-container").height() - stlFMFixedSize);
	$("#workflowtab").css("width", $(".flow-container").width());
	d3.select("#workflow").attr("width", $("#workflowtab").width() - 5 + "px")
			.attr("height",
					$("#workflowtab").height() - toolbarHeight - 5 + "px");

	$("#runningfmtab").css("height",
			$(".flow-container").height() - stlFMFixedSize);
	$("#runningfmtab").css("width", $(".flow-container").width());

	$("#runningFMTopContainer").css("height",
			Math.floor(($(".flow-container").height() - stlFMFixedSize) * 0.7));
	$("#runningFMTopContainer").css("width", $(".flow-container").width());

	$("#nonfunctionalStateContainer").css("height",
			Math.floor(($(".flow-container").height() - stlFMFixedSize) * 0.3));
	$("#nonfunctionalStateContainer")
			.css("width", $(".flow-container").width());

	if (force != null) {
		force.size([ $("#workflowtab").width() - 5,
				$("#workflowtab").height() - 5 ]);
	}

	$("#bpelGraphTab").css("height",
			$(".flow-container").height() - stlFMFixedSize);
	$("#bpelGraphTab").css("width", $(".flow-container").width());

}

$(window).resize(function() {
	resizeHandler();
});
resizeHandler();
