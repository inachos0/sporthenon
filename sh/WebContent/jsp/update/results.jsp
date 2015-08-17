<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.sporthenon.utils.ConfigUtils"%>
<%@ page import="com.sporthenon.utils.StringUtils"%>
<%
String lang = String.valueOf(session.getAttribute("locale"));
Calendar cal = Calendar.getInstance();
String today = StringUtils.toTextDate(new Timestamp(cal.getTimeInMillis()), lang, "dd/MM/yyyy");
cal.add(Calendar.DAY_OF_YEAR, -1);
String yesterday = StringUtils.toTextDate(new Timestamp(cal.getTimeInMillis()), lang, "dd/MM/yyyy");
%>
<jsp:include page="/jsp/common/header.jsp" />
<div id="update-results" class="update">
	<script type="text/javascript" src="/js/dropzone.js"></script>
	<jsp:include page="/jsp/update/toolbar.jsp" />
	<div class="fieldset">
		<div class="fstitle"><%=StringUtils.text("update.results", session).toUpperCase()%></div>
		<div class="fscontent" style="height:auto;">
			<div style="float:right;text-align:right;"><a href="javascript:loadDataDialog('country');"><%=StringUtils.text("country.codes", session)%></a><br/><a href="javascript:loadDataDialog('state');"><%=StringUtils.text("country.states", session)%></a><br/><a href="javascript:loadDataDialog('team');"><%=StringUtils.text("entity.TM", session)%></a></div>
			<!-- EVENT -->
			<div style="float:left;width:auto;margin-right:5px;">
			<fieldset style="height:140px;"><legend>Event</legend>
				<table>
					<tr><td colspan="5"><input type="text" id="sp" tabindex="1" name="<%=StringUtils.text("entity.SP.1", session)%>"/><a href="javascript:clearValue('sp');">[X]</a></td></tr>
					<tr><td><img alt="" src="/img/component/treeview/join.gif"/></td><td colspan="4"><input type="text" id="cp" tabindex="2" name="<%=StringUtils.text("entity.CP.1", session)%>"/><a href="javascript:clearValue('cp');">[X]</a></td></tr>
					<tr><td><img alt="" src="/img/component/treeview/empty.gif"/></td><td><img alt="" src="/img/component/treeview/join.gif"/></td><td colspan="3"><input type="text" id="ev" tabindex="3" name="<%=StringUtils.text("entity.EV.1", session)%> #1"/><a href="javascript:clearValue('ev');">[X]</a></td></tr>
					<tr><td><img alt="" src="/img/component/treeview/empty.gif"/></td><td><img alt="" src="/img/component/treeview/empty.gif"/></td><td><img alt="" src="/img/component/treeview/join.gif"/></td><td colspan="2"><input type="text" id="se" tabindex="4" name="<%=StringUtils.text("entity.EV.1", session)%> #2"/><a href="javascript:clearValue('se');">[X]</a></td></tr>
					<tr><td><img alt="" src="/img/component/treeview/empty.gif"/></td><td><img alt="" src="/img/component/treeview/empty.gif"/></td><td><img alt="" src="/img/component/treeview/empty.gif"/></td><td><img alt="" src="/img/component/treeview/join.gif"/></td><td><input type="text" tabindex="5" id="se2" name="<%=StringUtils.text("entity.EV.1", session)%> #3"/><a href="javascript:clearValue('se2');">[X]</a></td></tr>
				</table>
			</fieldset>
			</div>
			<!-- DATES -->
			<div style="float:left;width:auto;margin-right:5px;">
			<fieldset style="height:140px;"><legend>Dates</legend>
				<table>
					<tr><td><input type="text" id="yr" tabindex="6" name="<%=StringUtils.text("entity.YR.1", session)%>"/><a href="javascript:clearValue('yr');">[X]</a></td>
					<td><input id='prevbtn' type='button' class='button' onclick='loadResult("prev");' value=''/></td>
					<td><input id='nextbtn' type='button' class='button' onclick='loadResult("next");' value=''/></td></tr>
				</table>
				<table>
					<tr><td><input type="text" id="dt1" tabindex="7" name="<%=StringUtils.text("date", session)%> #1"/><a href="javascript:clearValue('dt1');">[X]</a><br/><a href="#" onclick="$('dt1').value='<%=today%>';$('dt1').addClassName('completed2');"><%=StringUtils.text("today", session)%></a>&nbsp;<a href="#" onclick="$('dt1').value='<%=yesterday%>';$('dt1').addClassName('completed2');"><%=StringUtils.text("yesterday", session)%></a></td>
					<td>&nbsp;<input type="text" id="dt2" tabindex="8" name="<%=StringUtils.text("date", session)%> #2"/><a href="javascript:clearValue('dt2');">[X]</a><br/><a href="#" onclick="$('dt2').value='<%=today%>';$('dt2').addClassName('completed2');"><%=StringUtils.text("today", session)%></a>&nbsp;<a href="#" onclick="$('dt2').value='<%=yesterday%>';$('dt2').addClassName('completed2');"><%=StringUtils.text("yesterday", session)%></a></td></tr>
				</table>
			</fieldset>
			</div>
			<!-- PHOTO -->
			<div id="imgzone" style="left:950px;">
				<fieldset style="height:140px;"><legend><%=StringUtils.text("photo", session)%></legend>
					<div id="dz-file"><p><%=StringUtils.text("click.drag.drop", session)%></p></div>	
				</fieldset>
			</div>
			<div id="currentimg"></div>
			<!-- PLACES/VENUES -->
			<div style="clear:left;float:left;width:auto;margin-right:5px;margin-top:8px;">
			<fieldset style="height:145px;"><legend>Places</legend>
				<table>
					<tr><td><input type="text" id="pl1" tabindex="9" name="<%=StringUtils.text("venue.city", session)%> #1"/><a href="javascript:clearValue('pl1');">[X]</a></td></tr>
					<tr><td><input type="text" id="pl2" tabindex="10" name="<%=StringUtils.text("venue.city", session)%> #2"/><a href="javascript:clearValue('pl2');">[X]</a></td></tr>
				</table>
			</fieldset>
			</div>
			<!-- OTHER -->
			<div style="float:left;width:auto;margin-right:5px;margin-top:8px;">
			<fieldset style="height:145px;"><legend>Other Info</legend>
				<table>
					<tr><td><input type="text" id="exa" tabindex="11" name="<%=StringUtils.text("tie", session)%>" style="width:150px;"/></td></tr>
				</table>
				<table>
					<tr><td><input type="text" id="cmt" tabindex="12" name="<%=StringUtils.text("comment", session)%>" style="width:500px;"/></td></tr>
				</table>
				<table>
					<tr><td><textarea id="exl" tabindex="14" name="<%=StringUtils.text("extlinks", session)%>" cols="100" rows="3" style="width:500px;"><%=StringUtils.text("extlinks", session)%></textarea></td></tr>
				</table>
			</fieldset>
			</div>
			<!-- RANKINGS -->
			<div style="clear:left;float:left;width:auto;margin-right:5px;margin-top:8px;">
			<fieldset><legend>Rankings</legend>
				<table style="margin-top:0px;">
					<tr><td><input type="text" id="rk1" tabindex="100" name="<%=StringUtils.text("rank.1", session)%>"/><a href="javascript:clearValue('rk1');">[X]</a></td><td><a href="javascript:initPersonList(1);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs1" tabindex="101" name="<%=StringUtils.text("result.score", session)%>" style="width:120px;"/></td><td><input type="text" id="rk11" tabindex="120" name="<%=StringUtils.text("rank.11", session)%>"/><a href="javascript:clearValue('rk11');">[X]</a></td><td><a href="javascript:initPersonList(11);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs11" tabindex="121" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk2" tabindex="102" name="<%=StringUtils.text("rank.2", session)%>"/><a href="javascript:clearValue('rk2');">[X]</a></td><td><a href="javascript:initPersonList(2);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs2" tabindex="103" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk12" tabindex="122" name="<%=StringUtils.text("rank.12", session)%>"/><a href="javascript:clearValue('rk12');">[X]</a></td><td><a href="javascript:initPersonList(12);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs12" tabindex="123" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk3" tabindex="104" name="<%=StringUtils.text("rank.3", session)%>"/><a href="javascript:clearValue('rk3');">[X]</a></td><td><a href="javascript:initPersonList(3);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs3" tabindex="105" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk13" tabindex="124" name="<%=StringUtils.text("rank.13", session)%>"/><a href="javascript:clearValue('rk13');">[X]</a></td><td><a href="javascript:initPersonList(13);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs13" tabindex="125" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk4" tabindex="106" name="<%=StringUtils.text("rank.4", session)%>"/><a href="javascript:clearValue('rk4');">[X]</a></td><td><a href="javascript:initPersonList(4);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs4" tabindex="107" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk14" tabindex="126" name="<%=StringUtils.text("rank.14", session)%>"/><a href="javascript:clearValue('rk14');">[X]</a></td><td><a href="javascript:initPersonList(14);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs14" tabindex="127" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk5" tabindex="108" name="<%=StringUtils.text("rank.5", session)%>"/><a href="javascript:clearValue('rk5');">[X]</a></td><td><a href="javascript:initPersonList(5);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs5" tabindex="109" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk15" tabindex="128" name="<%=StringUtils.text("rank.15", session)%>"/><a href="javascript:clearValue('rk15');">[X]</a></td><td><a href="javascript:initPersonList(15);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs15" tabindex="129" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk6" tabindex="110" name="<%=StringUtils.text("rank.6", session)%>"/><a href="javascript:clearValue('rk6');">[X]</a></td><td><a href="javascript:initPersonList(6);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs6" tabindex="111" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk16" tabindex="130" name="<%=StringUtils.text("rank.16", session)%>"/><a href="javascript:clearValue('rk16');">[X]</a></td><td><a href="javascript:initPersonList(16);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs16" tabindex="131" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk7" tabindex="112" name="<%=StringUtils.text("rank.7", session)%>"/><a href="javascript:clearValue('rk7');">[X]</a></td><td><a href="javascript:initPersonList(7);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs7" tabindex="113" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk17" tabindex="132" name="<%=StringUtils.text("rank.17", session)%>"/><a href="javascript:clearValue('rk17');">[X]</a></td><td><a href="javascript:initPersonList(17);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs17" tabindex="133" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk8" tabindex="114" name="<%=StringUtils.text("rank.8", session)%>"/><a href="javascript:clearValue('rk8');">[X]</a></td><td><a href="javascript:initPersonList(8);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs8" tabindex="115" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk18" tabindex="134" name="<%=StringUtils.text("rank.18", session)%>"/><a href="javascript:clearValue('rk18');">[X]</a></td><td><a href="javascript:initPersonList(18);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs18" tabindex="135" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk9" tabindex="116" name="<%=StringUtils.text("rank.9", session)%>"/><a href="javascript:clearValue('rk9');">[X]</a></td><td><a href="javascript:initPersonList(9);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs9" tabindex="117" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk19" tabindex="136" name="<%=StringUtils.text("rank.19", session)%>"/><a href="javascript:clearValue('rk19');">[X]</a></td><td><a href="javascript:initPersonList(19);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs19" tabindex="137" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
					<tr><td><input type="text" id="rk10" tabindex="118" name="<%=StringUtils.text("rank.10", session)%>"/><a href="javascript:clearValue('rk10');">[X]</a></td><td><a href="javascript:initPersonList(10);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs10" tabindex="119" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td><td><input type="text" id="rk20" tabindex="138" name="<%=StringUtils.text("rank.20", session)%>"/><a href="javascript:clearValue('rk20');">[X]</a></td><td><a href="javascript:initPersonList(20);"><img src="/img/update/personlist.png"/></a></td><td>&nbsp;<input type="text" id="rs20" tabindex="139" name="<%=StringUtils.text("entity.RS.1", session)%>" style="width:120px;"/></td></tr>
				</table>	
			</fieldset>
			</div>
			<!-- DRAW -->
			<div style="clear:left;width:800px;margin-right:5px;padding-top:10px;">
			<fieldset><legend><table><tr><td><input type="checkbox" id="cbdraw" onclick="toggleDraw();"/></td><td><label for="cbdraw">Add Draw</label></td></tr></table></legend>
				<table id="draw" style="display:none;">
					<tr><td><input type="text" id="qf1w" tabindex="1000" name="<%=StringUtils.text("quarterfinal", session)%> #1 - <%=StringUtils.text("winner", session)%>"/><a href="javascript:clearValue('qf1w');">[X]</a></td></tr>
					<tr><td><input type="text" id="qf1l" tabindex="1001" name="<%=StringUtils.text("quarterfinal", session)%> #1 - <%=StringUtils.text("loser", session)%>"/><a href="javascript:clearValue('qf1l');">[X]</a></td></tr>
					<tr><td style="text-align:center;">&nbsp;<input type="text" id="qf1rs" tabindex="1002" name="<%=StringUtils.text("score", session)%>" style="width:150px;"/></td></tr>
					<tr><td><input type="text" id="qf2w" tabindex="1003" name="<%=StringUtils.text("quarterfinal", session)%> #2 - <%=StringUtils.text("winner", session)%>"/><a href="javascript:clearValue('qf2w');">[X]</a></td><td style="padding-left:50px;"><input type="text" id="sf1w" tabindex="1012" name="<%=StringUtils.text("semifinal", session)%> #1 - <%=StringUtils.text("winner", session)%>"/><a href="javascript:clearValue('sf1w');">[X]</a></td></tr>
					<tr><td><input type="text" id="qf2l" tabindex="1004" name="<%=StringUtils.text("quarterfinal", session)%> #2 - <%=StringUtils.text("loser", session)%>"/><a href="javascript:clearValue('qf2l');">[X]</a></td><td style="padding-left:50px;"><input type="text" id="sf1l" tabindex="1013" name="<%=StringUtils.text("semifinal", session)%> #1 - <%=StringUtils.text("loser", session)%>"/><a href="javascript:clearValue('sf1l');">[X]</a></td></tr>
					<tr><td style="text-align:center;">&nbsp;<input type="text" id="qf2rs" tabindex="1005" name="<%=StringUtils.text("score", session)%>" style="width:150px;"/></td><td style="padding-left:50px;text-align:center;">&nbsp;<input type="text" id="sf1rs" tabindex="1014" name="<%=StringUtils.text("score", session)%>" style="width:150px;"/></td></tr>
					<tr><td><input type="text" id="qf3w" tabindex="1006" name="<%=StringUtils.text("quarterfinal", session)%> #3 - <%=StringUtils.text("winner", session)%>"/><a href="javascript:clearValue('qf3w');">[X]</a></td><td style="padding-left:50px;"><input type="text" id="sf2w" tabindex="1015" name="<%=StringUtils.text("semifinal", session)%> #2 - <%=StringUtils.text("winner", session)%>"/><a href="javascript:clearValue('sf2w');">[X]</a></td></tr>
					<tr><td><input type="text" id="qf3l" tabindex="1007" name="<%=StringUtils.text("quarterfinal", session)%> #3 - <%=StringUtils.text("loser", session)%>"/><a href="javascript:clearValue('qf3l');">[X]</a></td><td style="padding-left:50px;"><input type="text" id="sf2l" tabindex="1016" name="<%=StringUtils.text("semifinal", session)%> #2 - <%=StringUtils.text("loser", session)%>"/><a href="javascript:clearValue('sf2l');">[X]</a></td></tr>
					<tr><td style="text-align:center;">&nbsp;<input type="text" id="qf3rs" tabindex="1008" name="<%=StringUtils.text("score", session)%>" style="width:150px;"/></td><td style="padding-left:50px;text-align:center;">&nbsp;<input type="text" id="sf2rs" tabindex="1017" name="<%=StringUtils.text("score", session)%>" style="width:150px;"/></td></tr>
					<tr><td><input type="text" id="qf4w" tabindex="1009" name="<%=StringUtils.text("quarterfinal", session)%> #4 - <%=StringUtils.text("winner", session)%>"/><a href="javascript:clearValue('qf4w');">[X]</a></td><td style="padding-left:50px;"><input type="text" id="thdw" tabindex="1018" name="<%=StringUtils.text("third.place", session)%> - <%=StringUtils.text("winner", session)%>"/><a href="javascript:clearValue('thdw');">[X]</a></td></tr>
					<tr><td><input type="text" id="qf4l" tabindex="1010" name="<%=StringUtils.text("quarterfinal", session)%> #4 - <%=StringUtils.text("loser", session)%>"/><a href="javascript:clearValue('qf4l');">[X]</a></td><td style="padding-left:50px;"><input type="text" id="thdl" tabindex="1019" name="<%=StringUtils.text("third.place", session)%> - <%=StringUtils.text("loser", session)%>"/><a href="javascript:clearValue('thdl');">[X]</a></td></tr>
					<tr><td style="text-align:center;">&nbsp;<input type="text" id="qf4rs" tabindex="1011" name="<%=StringUtils.text("score", session)%>" style="width:150px;"/></td><td style="padding-left:50px;text-align:center;">&nbsp;<input type="text" id="thdrs" tabindex="1020" name="<%=StringUtils.text("score", session)%>" style="width:150px;"/></td></tr>
				</table>
			</fieldset>
			</div>
			<!-- BUTTON PANEL -->
			<table class="toolbar" style="float:right;">
				<tr>
					<td><input id="upd-add" type="button" class="button upd-add" onclick="tValues['id']=null;saveResult();" value="<%=StringUtils.text("button.add", session)%>"/></td>
					<td><input id="upd-modify" type="button" class="button upd-modify" onclick="saveResult();" value="<%=StringUtils.text("button.modify", session)%>"/></td>
				</tr>
			</table><br/>
			<table class="toolbar" style="clear:right;float:right;margin-top:5px;">
				<tr>
					<td><input id="upd-first" type="button" class="button upd-first" onclick="loadResult('first');" value="<%=StringUtils.text("first", session)%>"/></td>
					<td><input id="upd-previous" type="button" class="button upd-previous" onclick="loadResult('prev');" value="<%=StringUtils.text("previous", session)%>"/></td>
					<td><input id="upd-find" type="button" class="button upd-find" onclick="findEntity();" value="<%=StringUtils.text("find", session)%>"/></td>
					<td><input id="upd-next" type="button" class="button upd-next" onclick="loadResult('prev');" value="<%=StringUtils.text("next", session)%>"/></td>
					<td><input id="upd-last" type="button" class="button upd-last" onclick="loadEntity('last');" value="<%=StringUtils.text("last", session)%>"/></td>
				</tr>
			</table>
			<div id="msg" style="float:left;"></div>
		</div>
	</div>
	<br/>
	<span class="small"><%=StringUtils.text("for.any.request", session)%>&nbsp;:&nbsp;<a href="mailto:admin@sporthenon.com">admin@sporthenon.com</a></span>
</div>
<script type="text/javascript"><!--
window.onload = function() {
	initUpdateResults("<%=request.getAttribute("value")%>");
}
--></script>
<jsp:include page="/jsp/common/footer.jsp" />