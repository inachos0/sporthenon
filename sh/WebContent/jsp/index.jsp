<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ page import="com.sporthenon.db.DatabaseHelper"%>
<%@ page import="com.sporthenon.db.entity.Sport"%>
<%@ page import="com.sporthenon.db.function.LastUpdateBean"%>
<%@ page import="com.sporthenon.db.function.StatisticsBean"%>
<%@ page import="com.sporthenon.utils.res.ResourceUtils"%>
<%@ page import="com.sporthenon.utils.StringUtils"%>
<%@ page import="com.sporthenon.web.HtmlConverter"%>
<%@ page import="com.sporthenon.web.servlet.IndexServlet"%>
<jsp:include page="/jsp/common/header.jsp" />
<script type="text/javascript"><!--
	t1 = <%=System.currentTimeMillis()%>;
--></script>
<div id="home">
<div class="homecontent">
	<!-- PRESENTATION -->
	<div class="fieldset">
		<div class="fstitle info"><%=StringUtils.text("title.presentation", session)%></div>
		<div class="fscontent">
			<h3><%=StringUtils.text("sporthenon.welcome", session)%></h3><br/>
			<img src='/img/bullet.gif' alt='-'/>&nbsp;<%=StringUtils.text("sporthenon.desc", session)%></a><br/>
			<hr/><img src='/img/bullet.gif' alt='-'/>&nbsp;<b><%=StringUtils.text("site.topics", session)%></b><br/>
			<div id="topics"><table><tr>
				<td class="results" onclick="location.href='/results';" onmouseover='overTopic(TX_DESC_RESULTS);' onmouseout="$('details').hide();"><%=StringUtils.text("menu.results", session)%></td>
				<td class="olympics" onclick="location.href='/olympics';" onmouseover='overTopic(TX_DESC_OLYMPICS);' onmouseout="$('details').hide();"><%=StringUtils.text("menu.olympics", session)%></td>
				<td class="usleagues" onclick="location.href='/usleagues';" onmouseover='overTopic(TX_DESC_USLEAGUES);' onmouseout="$('details').hide();"><%=StringUtils.text("menu.usleagues", session)%></td>
				<td id="details" style="display:none;"></td></tr></table></div>
			<hr/><img src='/img/bullet.gif' alt='-'/>&nbsp;<b><%=StringUtils.text("access.sport", session)%></b><select style="margin-left:10px;" onchange="location.href=this.value;"><option value="">--- Choose sport ---</option>
			<%
				String lang = String.valueOf(session.getAttribute("locale"));
				for (Sport sp : (List<Sport>) DatabaseHelper.execute("from Sport order by label" + (lang != null && !lang.equalsIgnoreCase(ResourceUtils.LGDEFAULT) ? lang.toUpperCase() : "")))
					out.print("<option value=\"" + StringUtils.urlEscape("/sport/" + sp.getLabel() + "/" + StringUtils.encode("SP-" + sp.getId())) + "\">" + sp.getLabel(lang) + "</option>");
			%>
			</select><br/>
			<div id="sports" class="slider"><%@include file="../html/slider.html"%></div>
		</div>
	</div>
	<!-- LAST UPDATES -->
	<div class="fieldset">
		<div class="fstitle lastupdates"><%=StringUtils.text("title.last.results", session)%></div>
		<div class="fscontent">
		<div id="dupdates">
		<table id="tlast" class="tsort"><thead><tr class='rsort'>
			<th onclick="sort('tlast', this, 0);"><%=StringUtils.text("year", session)%></th>
			<th onclick="sort('tlast', this, 1);"><%=StringUtils.text("sport", session)%></th>
			<th onclick="sort('tlast', this, 2);"><%=StringUtils.text("event", session)%></th>
			<th onclick="sort('tlast', this, 3);"><%=StringUtils.text("entity.RS.1", session)%></th>
			<th id="tlast-dtcol" class="sorted desc" onclick="sort('tlast', this, 4);"><%=StringUtils.text("updated.on", session).replaceAll("\\s", "&nbsp;")%></th>
		</tr></thead><tbody class="tby" id="tb-tlast">
		<%
        	ArrayList<Object> lParams = new ArrayList<Object>();
        	lParams.add(new Integer(20));
        	lParams.add(new Integer(0));
        	lParams.add("_" + lang);
        	Collection coll = DatabaseHelper.call("LastUpdates", lParams);
        	out.print(HtmlConverter.convertLastUpdates(coll, 20, 0, lang));
        	Timestamp ts = null;
        	for (Object o : coll) {
        		LastUpdateBean bean = (LastUpdateBean) o;
        		if (ts == null || bean.getRsUpdate().compareTo(ts) > 0)
        			ts = bean.getRsUpdate();
        	}
        	request.setAttribute("lastupdate", StringUtils.toTextDate(ts, lang, "dd/MM/yyyy"));
		%></tbody></table></div></div>
	</div>
	<!-- STATISTICS -->
	<%
		ArrayList<StatisticsBean> lStats = new ArrayList(DatabaseHelper.call("Statistics", null));
		StatisticsBean stb = lStats.get(0);
	%>
	<div class="fieldset">
		<div class="fstitle statistics"><%=StringUtils.text("title.statistics", session)%></div>
		<div class="fscontent" style="height:440px;overflow:auto;">
			<div style="width:200px;margin-right:10px;float:left;">
			<table>
				<tr><th><%=StringUtils.text("entity.SP", session)%></th><td class="stat"><%=stb.getCountSport()%></td></tr>
				<tr><th><%=StringUtils.text("entity.EV", session)%></th><td class="stat"><%=stb.getCountEvent()%></td></tr>
				<tr><th><%=StringUtils.text("entity.RS", session)%></th><td class="stat"><%=stb.getCountResult()%></td></tr>
			</table><table style="margin-top:10px;">
				<tr><th><%=StringUtils.text("entity.PR", session)%></th><td class="stat"><%=stb.getCountPerson()%></td></tr>
				<tr><th><%=StringUtils.text("entity.TM", session)%></th><td class="stat"><%=stb.getCountTeam()%></td></tr>
				<tr><th><%=StringUtils.text("entity.CN", session)%></th><td class="stat"><%=stb.getCountCountry()%></td></tr>
				<tr><th><%=StringUtils.text("entity.CT", session)%></th><td class="stat"><%=stb.getCountCity()%></td></tr>
				<tr><th><%=StringUtils.text("entity.CX", session)%></th><td class="stat"><%=stb.getCountComplex()%></td></tr>
			</table>
			</div>
			<div style="float:left;">
			<table>
				<tr><th style="text-align:center;"><div style="float:left;margin-left:2px;font-weight:normal;"><a href="javascript:changeReport(-1);">&lt;&nbsp;<%=StringUtils.text("previous", session)%></a></div><div style="float:right;margin-right:2px;font-weight:normal;"><a href="javascript:changeReport(1);"><%=StringUtils.text("next", session)%>&nbsp;&gt;</a></div><span id="ctitle"><%=StringUtils.text("report.1", session)%></span></th></tr>
				<tr><td id="chart"></td></tr>
			</table>
			</div>
		</div>
	</div>
	<div style="clear:both;"></div>
</div>
</div>
<script type="text/javascript" src="js/RGraph.common.core.js"></script>
<script type="text/javascript" src="js/RGraph.hbar.js"></script>
<script type="text/javascript" src="js/RGraph.pie.js"></script>
<script type="text/javascript"><!--
var ctitle = ['<%=StringUtils.text("report.1", session)%>', '<%=StringUtils.text("report.2", session)%>', '<%=StringUtils.text("report.3", session)%>'];
<%
String lang_ = (lang != null && !lang.equalsIgnoreCase(ResourceUtils.LGDEFAULT) ? "_" + lang : "");
List<Object[]> cSportStats = DatabaseHelper.executeNative(IndexServlet.REPORT_QUERY1.replaceAll("#LANG#", lang_));
for (Object[] t : cSportStats) {
	out.print("clabel.push('" + String.valueOf(t[0]) + "');");
	out.print("cdata.push(" + t[1] + ");");
}
%>
window.onload = function() {
	tCurrentSortedCol['tlast'] = $('tlast-dtcol');
	initSliderHome("<%=IndexServlet.getSportDivs(lang)%>");
	t2 = <%=System.currentTimeMillis()%>;
	handleRender();
	loadReport(cdata, clabel, ccolor[0]);
}
--></script>
<jsp:include page="/jsp/common/footer.jsp" />