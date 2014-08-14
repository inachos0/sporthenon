<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="com.sporthenon.utils.ConfigUtils"%>
<%@page import="com.sporthenon.utils.StringUtils"%>
<jsp:include page="/jsp/common/header.jsp" />
<script type="text/javascript" src="js/canvas2image.js"></script>
<script type="text/javascript" src="js/canvastext.js"></script>
<script type="text/javascript" src="js/flotr.js"></script>
<div id="home">
	<div class="right">
		<div class="fieldset" style="margin-top:0px;">
			<div class="fstitle downloads"><%=StringUtils.text("downloads", session)%></div>
			<div class="fscontent"><table"><tr><td><img src="img/project/download.png"/></td><td><a href="#"><b>Télécharger la dernière version (<%=ConfigUtils.getProperty("version")%>)</b></a></td></tr></table><br/><br/><a href="#">Versions précédentes</a></div>
		</div>
		
		<div class="fieldset">
			<div class="fstitle reportbug"><%=StringUtils.text("report.bug", session)%></div>
			<div class="fscontent"><a href="https://code.google.com/p/sporthenon/issues/list" target="_blank">https://code.google.com/p/sporthenon/issues/list</a></div>
		</div>
		
		<div class="fieldset">
		<div class="fstitle contributors"><%=StringUtils.text("contributors", session)%></div>
		<div class="fscontent">-</div>
		</div>
	</div>
	<div class="left">
		<div class="fieldset">
		<div class="fstitle technicalinfo"><%=StringUtils.text("technical.info", session)%></div>
		<div class="fscontent">
			<table>
				<tr><th>Tool</th><th>Version</th><th>Description</th><th>Website</th></tr>
				<tr><th colspan="4" style="text-align:center;">Programming</th></tr>
				<tr><td>Java Development Kit</td><td>1.6.0_18</td><td>Required JDK + JRE</td><td><a href="http://www.oracle.com/technetwork/java/index.html" target="_blank">http://www.oracle.com/technetwork/java/index.html</a></td></tr>
				<tr><td>Eclipse</td><td>4.2.0 (Juno)</td><td>J2EE development platform</td><td><a href="http://www.eclipse.org/" target="_blank">http://www.eclipse.org</a></td></tr>
				<tr><td>NSIS</td><td>2.46</td><td>Setup-wizard maker</td><td><a href="http://nsis.sourceforge.net/" target="_blank">http://nsis.sourceforge.net</a></td></tr>
				<tr><td>Jsoup</td><td>1.7.2</td><td>Java HTML Parser</td><td><a href="http://jsoup.org/" target="_blank">http://jsoup.org</a></td></tr>
				<tr><td>Janel</td><td>4.0.4</td><td>Executable maker (from JAR)</td><td><a href="http://sourceforge.net/projects/janel/" target="_blank">http://sourceforge.net/projects/janel</a></td></tr>
				<tr><td>Prototype</td><td>1.6.1</td><td>Javascript framework</td><td><a href="http://prototypejs.org/" target="_blank">http://prototypejs.org</a></td></tr>
				<tr><td>Script.aculo.us</td><td>1.8.3</td><td>Javascript framework (UI)</td><td><a href="http://script.aculo.us/" target="_blank">http://script.aculo.us</a></td></tr>
				<tr><td>Subclipse</td><td>1.8.20</td><td>SVN plug-in for Eclipse</td><td><a href="http://subclipse.tigris.org/" target="_blank">http://subclipse.tigris.org</a></td></tr>
				<tr><th colspan="4" style="text-align:center;">Database &amp; Hosting</th></tr>
				<tr><td>CentOS</td><td>5.6</td><td>Linux server</td><td><a href="http://www.centos.org/" target="_blank">http://www.centos.org</a></td></tr>
				<tr><td>Glassfish</td><td>3.1.2</td><td>Java application server</td><td><a href="http://glassfish.java.net/" target="_blank">http://glassfish.java.net</a></td></tr>
				<tr><td>PostgreSQL</td><td>8.4.2.1</td><td>Database server</td><td><a href="http://www.postgresql.org/" target="_blank">http://www.postgresql.org</a></td></tr>
			</table>
		</div>
		</div>
		
		<div class="fieldset">
		<div class="fstitle statistics"><%=StringUtils.text("statistics", session)%></div>
		<div class="fscontent"><select id="charts" style="width:250px;margin:2px;" onchange="loadChart();"><option value="0">Number of Requests per Type</option><option value="1">Number of Requests per Sport</option></select><table><tr><td style="vertical-align:top;border:none;width:250px;"><table id="charttxt"><tr><td></td></tr></table></td><td style="vertical-align:top;border:none;"><div id="chart"></div></td></tr></table></div>
		</div>
	</div>
</div>
<script type="text/javascript">
window.onload = function() {
	loadChart();	
}
</script>
<jsp:include page="/jsp/common/footer.jsp" />