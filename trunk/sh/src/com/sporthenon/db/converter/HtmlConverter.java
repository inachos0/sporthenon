package com.sporthenon.db.converter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.sporthenon.db.DatabaseHelper;
import com.sporthenon.db.entity.Athlete;
import com.sporthenon.db.entity.Championship;
import com.sporthenon.db.entity.City;
import com.sporthenon.db.entity.Complex;
import com.sporthenon.db.entity.Country;
import com.sporthenon.db.entity.Draw;
import com.sporthenon.db.entity.Event;
import com.sporthenon.db.entity.HallOfFame;
import com.sporthenon.db.entity.OlympicRanking;
import com.sporthenon.db.entity.Olympics;
import com.sporthenon.db.entity.Record;
import com.sporthenon.db.entity.Result;
import com.sporthenon.db.entity.RetiredNumber;
import com.sporthenon.db.entity.Sport;
import com.sporthenon.db.entity.State;
import com.sporthenon.db.entity.Team;
import com.sporthenon.db.entity.TeamStadium;
import com.sporthenon.db.entity.WinLoss;
import com.sporthenon.db.entity.Year;
import com.sporthenon.db.entity.meta.RefItem;
import com.sporthenon.db.entity.meta.TreeItem;
import com.sporthenon.db.function.DrawBean;
import com.sporthenon.db.function.HallOfFameBean;
import com.sporthenon.db.function.LastUpdateBean;
import com.sporthenon.db.function.OlympicMedalsBean;
import com.sporthenon.db.function.OlympicRankingsBean;
import com.sporthenon.db.function.ResultsBean;
import com.sporthenon.db.function.RetiredNumberBean;
import com.sporthenon.db.function.TeamStadiumBean;
import com.sporthenon.db.function.USChampionshipsBean;
import com.sporthenon.db.function.USRecordsBean;
import com.sporthenon.db.function.WinLossBean;
import com.sporthenon.db.function.WinRecordsBean;
import com.sporthenon.utils.HtmlUtils;
import com.sporthenon.utils.ImageUtils;
import com.sporthenon.utils.StringUtils;
import com.sporthenon.utils.res.ResourceUtils;
import com.sporthenon.web.servlet.USLeaguesServlet;

public class HtmlConverter {

	public static final short HEADER_RESULTS = 0;
	public static final short HEADER_OLYMPICS_INDIVIDUAL = 1;
	public static final short HEADER_OLYMPICS_COUNTRY = 2;
	public static final short HEADER_US_LEAGUES_RETNUM = 3;
	public static final short HEADER_US_LEAGUES_TEAMSTADIUM = 4;
	public static final short HEADER_US_LEAGUES_WINLOSS = 5;
	public static final short HEADER_US_LEAGUES_HOF = 6;
	public static final short HEADER_US_LEAGUES_CHAMPIONSHIP = 7;
	public static final short HEADER_US_LEAGUES_RECORD = 8;
	public static final short HEADER_SEARCH = 9;
	public static final short HEADER_REF = 10;

	private static String getResultsEntity(int type, Integer rank, String str1, String str2, String year) {
		String s = null;
		if (rank != null && rank > 0) {
			if (type < 10)
				s = HtmlUtils.writeLink(Athlete.alias, rank, StringUtils.toFullName(str1, str2), null);
			else if (type == 50) {
				String img = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, rank, ImageUtils.SIZE_SMALL, year, null);
				s = HtmlUtils.writeLink(Team.alias, rank, str2, null);
				s = HtmlUtils.writeImgTable(img, s);
			}
			else if (type == 99) {
				String img = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, rank, ImageUtils.SIZE_SMALL, year, null);
				s = HtmlUtils.writeLink(Country.alias, rank, str2, null);
				s = HtmlUtils.writeImgTable(img, s);
			}
		}
		return s;
	}

	private static String getResultsEntityRel(Integer rel1Id, String rel1Code, String rel1Label, Integer rel2Id, String rel2Code, String rel2Label, boolean isRel1, boolean isRel2, String year) {
		StringBuffer html = new StringBuffer();
		String tmpImg = null;
		if (rel2Id != null && rel2Id > 0) {
			tmpImg = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, rel2Id, ImageUtils.SIZE_SMALL, year, null);
			String s = HtmlUtils.writeLink(Country.alias, rel2Id, rel2Code, rel2Label);
			html.append("<td>").append(HtmlUtils.writeImgTable(tmpImg, s)).append("</td>");
		}
		else if (isRel2)
			html.append("<td>" + StringUtils.EMPTY + "</td>");
		if (rel1Id != null && rel1Id > 0) {
			tmpImg = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, rel1Id, ImageUtils.SIZE_SMALL, year, null);
			String s = HtmlUtils.writeLink(Team.alias, rel1Id, rel1Label, null);
			String s_ = HtmlUtils.writeImgTable(tmpImg, s);
			if (!s_.matches(".*\\<img.*"))
				s_ = "<table><tr><td>" + s_ + "</td></tr></table>";
			html.append("<td>").append(s_).append("</td>");
		}
		else if (isRel1)
			html.append("<td>" + StringUtils.EMPTY + "</td>");
		return html.toString();
	}

	public static StringBuffer getHeader(short type, Collection<Object> params, String lang) throws Exception {
		ArrayList<Object> lstParams = new ArrayList<Object>(params);
		HashMap<String, String> hHeader = new HashMap<String, String>();
		Logger.getLogger("sh").debug("Header - " + type + " - " + params);
		hHeader.put("info", "#INFO#");
		if (type == HEADER_RESULTS) {
			Sport sp = (Sport) DatabaseHelper.loadEntity(Sport.class, lstParams.get(0));
			Championship cp = (Championship) DatabaseHelper.loadEntity(Championship.class, lstParams.get(1));
			Event ev = (Event) DatabaseHelper.loadEntity(Event.class, lstParams.get(2));
			Event se = (Event) DatabaseHelper.loadEntity(Event.class, lstParams.get(3));
			Event se2 = (Event) DatabaseHelper.loadEntity(Event.class, lstParams.get(4));
			ArrayList<String> lstYears = DatabaseHelper.loadLabels(Year.class, String.valueOf(lstParams.get(5)), lang);
			hHeader.put("title", sp.getLabel(lang) + "&nbsp;-&nbsp;" + cp.getLabel(lang) + (ev != null ? "&nbsp;-&nbsp;" + ev.getLabel(lang) + (se != null ? "&nbsp;-&nbsp;" + se.getLabel(lang) : "") + (se2 != null ? "&nbsp;-&nbsp;" + se2.getLabel(lang) : "") : ""));
			hHeader.put("url", HtmlUtils.writeURL("results", lstParams.toString()));
			hHeader.put("item0", "<table><tr><td><img alt='Results' src='img/menu/dbresults.png'/></td><td>&nbsp;<a href='results'>" + ResourceUtils.getText("menu.results", lang) + "</a></td></tr></table>");
			hHeader.put("item1", HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_SPORT, sp.getId(), ImageUtils.SIZE_SMALL, null, sp.getLabel(lang)), HtmlUtils.writeLink(Sport.alias, sp.getId(), sp.getLabel(lang), null)));
			hHeader.put("item2", HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_CHAMPIONSHIP, cp.getId(), ImageUtils.SIZE_SMALL, null, cp.getLabel(lang)), HtmlUtils.writeLink(Championship.alias, cp.getId(), cp.getLabel(lang), null)));
			if (ev != null)
				hHeader.put("item3", HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_EVENT, ev.getId(), ImageUtils.SIZE_SMALL, null, ev.getLabel(lang)), HtmlUtils.writeLink(Event.alias, ev.getId(), ev.getLabel(lang), null)));
			if (se != null)
				hHeader.put("item4", HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_EVENT, se.getId(), ImageUtils.SIZE_SMALL, null, se.getLabel(lang)), HtmlUtils.writeLink(Event.alias, se.getId(), se.getLabel(lang), null)));
			if (se2 != null)
				hHeader.put("item5", HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_EVENT, se2.getId(), ImageUtils.SIZE_SMALL, null, se2.getLabel(lang)), HtmlUtils.writeLink(Event.alias, se2.getId(), se2.getLabel(lang), null)));
			hHeader.put("years", (lstYears.isEmpty() ? "" : (lstYears.size() == 1 ? "(" + lstYears.get(0) + ")" : HtmlUtils.writeTip(Year.alias, lstYears))));
		}
		else if (type == HEADER_OLYMPICS_INDIVIDUAL) {
			String olId = String.valueOf(lstParams.get(0));
			ArrayList<String> lstOlympics = DatabaseHelper.loadLabelsFromQuery("select concat(concat(ol.year.label, ' - '), ol.city.label" + (lang != null && !lang.equalsIgnoreCase("en") ? lang.toUpperCase() : "") + ") from Olympics ol where ol.id in (" + olId + ")");
			Sport sp = (Sport) DatabaseHelper.loadEntity(Sport.class, lstParams.get(1));
			Event ev = (Event) DatabaseHelper.loadEntity(Event.class, lstParams.get(2));
			ArrayList<String> lstEvents = DatabaseHelper.loadLabels(Event.class, String.valueOf(lstParams.get(2)), lang);
			ArrayList<String> lstSubevents = DatabaseHelper.loadLabels(Event.class, String.valueOf(lstParams.get(3)), lang);
			hHeader.put("title", ResourceUtils.getText("entity.OL", lang) + "&nbsp;-&nbsp;" + sp.getLabel(lang) + (ev != null ? "&nbsp;-&nbsp;" + ev.getLabel(lang) : ""));
			hHeader.put("title", ResourceUtils.getText("menu.olympics", lang));
			hHeader.put("url", HtmlUtils.writeURL("olympics", "ind, " + lstParams.toString()));
			hHeader.put("item0", "<table><tr><td><img alt='Olympics' src='img/menu/dbolympics.png'/></td><td>&nbsp;<a href='olympics'>" + ResourceUtils.getText("menu.olympics", lang) + "</a></td></tr></table>");
			hHeader.put("item1", ResourceUtils.getText("event.results", lang));
			hHeader.put("item2", (lstOlympics.isEmpty() ? ResourceUtils.getText("all.olympic.games", lang) : (lstOlympics.size() == 1 ? HtmlUtils.writeLink(Olympics.alias, Integer.valueOf(olId), lstOlympics.get(0), null) : HtmlUtils.writeTip(Olympics.alias, lstOlympics) + " " + ResourceUtils.getText("x.olympic.games", lang))));
			hHeader.put("item3", HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_SPORT, sp.getId(), ImageUtils.SIZE_SMALL, null, sp.getLabel(lang)), HtmlUtils.writeLink(Sport.alias, sp.getId(), sp.getLabel(lang), null)));
			hHeader.put("item4", (lstEvents.isEmpty() ? ResourceUtils.getText("all.events", lang) : (lstEvents.size() == 1 ? lstEvents.get(0) : HtmlUtils.writeTip(Event.alias, lstEvents) + " " + ResourceUtils.getText("x.events", lang))));
			if (!lstSubevents.isEmpty())
				hHeader.put("item5", (lstSubevents.isEmpty() ? ResourceUtils.getText("all.events", lang) : (lstSubevents.size() == 1 ? lstSubevents.get(0) : HtmlUtils.writeTip(Event.alias, lstSubevents) + " " + ResourceUtils.getText("x.events", lang))));
		}
		else if (type == HEADER_OLYMPICS_COUNTRY) {
			String olId = String.valueOf(lstParams.get(0));
			String cnId = String.valueOf(lstParams.get(1));
			ArrayList<String> lstOlympics = DatabaseHelper.loadLabelsFromQuery("select concat(concat(ol.year.label, ' - '), ol.city.label) from Olympics ol where ol.id in (" + olId + ")");
			ArrayList<String> lstCountries = DatabaseHelper.loadLabels(Country.class, cnId, lang);
			hHeader.put("title", ResourceUtils.getText("entity.OL", lang) + "&nbsp;-&nbsp;Medals&nbsp;Table");
			hHeader.put("title", ResourceUtils.getText("menu.olympics", lang));
			hHeader.put("url", HtmlUtils.writeURL("olympics", "cnt, " + lstParams.toString()));
			hHeader.put("item0", "<table><tr><td><img alt='Olympics' src='img/menu/dbolympics.png'/></td><td>&nbsp;<a href='olympics'>" + ResourceUtils.getText("menu.olympics", lang) + "</a></td></tr></table>");
			hHeader.put("item1", ResourceUtils.getText("medals.tables", lang));
			hHeader.put("item2", (lstOlympics.isEmpty() ? ResourceUtils.getText("all.olympic.games", lang) : (lstOlympics.size() == 1 ? HtmlUtils.writeLink(Olympics.alias, Integer.valueOf(olId), lstOlympics.get(0), null) : HtmlUtils.writeTip(Olympics.alias, lstOlympics) + " " + ResourceUtils.getText("x.olympic.games", lang))));
			hHeader.put("item3", (lstCountries.isEmpty() ? ResourceUtils.getText("all.countries", lang) : (lstCountries.size() == 1 ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, Integer.valueOf(cnId), ImageUtils.SIZE_SMALL, null, lstCountries.get(0)), HtmlUtils.writeLink(Country.alias, Integer.valueOf(cnId), lstCountries.get(0), null)) : HtmlUtils.writeTip(Country.alias, lstCountries) + " " + ResourceUtils.getText("x.countries", lang))));
		}
		else if (type == HEADER_US_LEAGUES_RETNUM || type == HEADER_US_LEAGUES_CHAMPIONSHIP || type == HEADER_US_LEAGUES_HOF || type == HEADER_US_LEAGUES_RECORD || type == HEADER_US_LEAGUES_TEAMSTADIUM || type == HEADER_US_LEAGUES_WINLOSS) {
			String league = String.valueOf(lstParams.get(0));
			String leagueLabel = (league.equals("1") ? "NFL" : (league.equals("2") ? "NBA" : (league.equals("3") ? "NHL" : "MLB")));
			String typeLabel = (type == HEADER_US_LEAGUES_RETNUM ? ResourceUtils.getText("retired.numbers", lang) : (type == HEADER_US_LEAGUES_CHAMPIONSHIP ? ResourceUtils.getText("championships", lang) : (type == HEADER_US_LEAGUES_HOF ? ResourceUtils.getText("hall.fame", lang) : (type == HEADER_US_LEAGUES_RECORD ? ResourceUtils.getText("records", lang) : (type == HEADER_US_LEAGUES_TEAMSTADIUM ? ResourceUtils.getText("team.stadiums", lang) : ResourceUtils.getText("wins.losses", lang))))));
			Integer cpId = (league.equals("1") ? 51 : (league.equals("2") ? 54 : (league.equals("3") ? 55 : 56)));
			hHeader.put("title", ResourceUtils.getText("menu.usleagues", lang));
			hHeader.put("title", leagueLabel + "&nbsp;-&nbsp;" + typeLabel);
			hHeader.put("item0", "<table><tr><td><img alt='US leagues' src='img/menu/dbusleagues.png'/></td><td>&nbsp;<a href='usleagues'>" + ResourceUtils.getText("menu.usleagues", lang) + "</a></td></tr></table>");
			hHeader.put("item1", HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_CHAMPIONSHIP, cpId, ImageUtils.SIZE_SMALL, null, leagueLabel), HtmlUtils.writeLink(Championship.alias, cpId, leagueLabel, null)));
			hHeader.put("item2", typeLabel);
			if (type == HEADER_US_LEAGUES_RETNUM) {
				hHeader.put("url", HtmlUtils.writeURL("usleagues", USLeaguesServlet.TYPE_RETNUM + ", " + lstParams.toString()));
				ArrayList<String> lstTeams = DatabaseHelper.loadLabels(Team.class, String.valueOf(lstParams.get(1)), lang);
				hHeader.put("item3", (lstTeams.isEmpty() ? ResourceUtils.getText("all.teams", lang) : (lstTeams.size() == 1 ? lstTeams.get(0) : HtmlUtils.writeTip(Team.alias, lstTeams) + " " + ResourceUtils.getText("x.teams", lang))));
			}
			else if (type == HEADER_US_LEAGUES_TEAMSTADIUM) {
				hHeader.put("url", HtmlUtils.writeURL("usleagues", USLeaguesServlet.TYPE_TEAMSTADIUM + ", " + lstParams.toString()));
				ArrayList<String> lstTeams = DatabaseHelper.loadLabels(Team.class, String.valueOf(lstParams.get(1)), lang);
				hHeader.put("item3", (lstTeams.isEmpty() ? ResourceUtils.getText("all.teams", lang) : (lstTeams.size() == 1 ? lstTeams.get(0) : HtmlUtils.writeTip(Team.alias, lstTeams) + " " + ResourceUtils.getText("x.teams", lang))));
			}
			else if (type == HEADER_US_LEAGUES_WINLOSS) {
				hHeader.put("url", HtmlUtils.writeURL("usleagues", USLeaguesServlet.TYPE_WINLOSS + ", " + lstParams.toString()));
				ArrayList<String> lstTeams = DatabaseHelper.loadLabels(Team.class, String.valueOf(lstParams.get(1)), lang);
				hHeader.put("item3", (lstTeams.isEmpty() ? ResourceUtils.getText("all.teams", lang) : (lstTeams.size() == 1 ? lstTeams.get(0) : HtmlUtils.writeTip(Team.alias, lstTeams) + " " + ResourceUtils.getText("x.teams", lang))));
			}
			else if (type == HEADER_US_LEAGUES_HOF) {
				hHeader.put("url", HtmlUtils.writeURL("usleagues", USLeaguesServlet.TYPE_HOF + ", " + lstParams.toString()));
				ArrayList<String> lstYears = DatabaseHelper.loadLabels(Year.class, String.valueOf(lstParams.get(1)), lang);
				hHeader.put("item3", (lstYears.isEmpty() ? ResourceUtils.getText("all.years", lang) : (lstYears.size() == 1 ? lstYears.get(0) : HtmlUtils.writeTip(Year.alias, lstYears) + " " + ResourceUtils.getText("x.years", lang))));
			}
			else if (type == HEADER_US_LEAGUES_CHAMPIONSHIP) {
				hHeader.put("url", HtmlUtils.writeURL("usleagues", USLeaguesServlet.TYPE_CHAMPIONSHIP + ", " + lstParams.toString()));
				ArrayList<String> lstYears = DatabaseHelper.loadLabels(Year.class, String.valueOf(lstParams.get(2)), lang);
				hHeader.put("item3", (lstYears.isEmpty() ? ResourceUtils.getText("all.years", lang) : (lstYears.size() == 1 ? lstYears.get(0) : HtmlUtils.writeTip(Year.alias, lstYears) + " " + ResourceUtils.getText("x.years", lang))));
			}
			else if (type == HEADER_US_LEAGUES_RECORD) {
				hHeader.put("url", HtmlUtils.writeURL("usleagues", USLeaguesServlet.TYPE_RECORD + ", " + lstParams.toString()));
				ArrayList<String> lstCategories = DatabaseHelper.loadLabels(Event.class, String.valueOf(lstParams.get(3)), lang);
				String scope = String.valueOf(lstParams.get(4));
				String scope2 = String.valueOf(lstParams.get(5));
				hHeader.put("item3", (lstCategories.isEmpty() ? ResourceUtils.getText("all.categories", lang) : (lstCategories.size() == 1 ? lstCategories.get(0) : HtmlUtils.writeTip(Event.alias, lstCategories) + " " + ResourceUtils.getText("x.categories", lang))));
				hHeader.put("item4", (scope.equalsIgnoreCase("[All]") ? ResourceUtils.getText("all.scopes", lang) : scope.replaceAll("'", "")));
				hHeader.put("item5", (scope2.equalsIgnoreCase("[All]") ? ResourceUtils.getText("all.scopes", lang) : scope2.replaceAll("'", "")));
			}
		}
		else if (type == HEADER_REF) {
			String entity = String.valueOf(lstParams.get(0));
			hHeader.put("title", String.valueOf(lstParams.get(4)));
			hHeader.put("item0", "<table><tr><td><img alt='Ref' src='img/menu/dbref.png'/></td><td>&nbsp;" + ResourceUtils.getText("entity." + entity, lang) + "</td></tr></table>");
			hHeader.put("item1", hHeader.get("title"));
		}
		return HtmlUtils.writeHeader(hHeader, lang);
	}

	public static StringBuffer getWinRecords(String results, String lang) throws Exception {
		ArrayList<Object> lParams = new ArrayList<Object>();
		lParams.add(results);
		lParams.add("_" + lang);
		Collection<WinRecordsBean> list = DatabaseHelper.call("WinRecords", lParams);
		return HtmlUtils.writeWinRecTable(list, lang);
	}

	public static StringBuffer getRecordInfo(String type, int id, String lang) throws Exception {
		LinkedHashMap<String, String> hInfo = new LinkedHashMap<String, String>();
		hInfo.put("url", HtmlUtils.writeURL("ref", type + ", " + id));
		hInfo.put("info", "#INFO#");
		if (type.equals(Athlete.alias)) {
			List<Athlete> lAthlete = new ArrayList<Athlete>();
			Athlete e = (Athlete) DatabaseHelper.loadEntity(Athlete.class, id);
			if (e.getLink() != null && e.getLink() >= 0) {
				Athlete e_ = (Athlete) DatabaseHelper.loadEntity(Athlete.class, e.getLink());
				String wId = "(-1" + (e_ != null && e_.getId() > 0 ? "," + e_.getId() : "") + (e_ != null && e_.getLink() > 0 ? "," + e_.getLink() : "") + (e.getId() > 0 ? "," + e.getId() : "") + (e.getLink() > 0 ? "," + e.getLink() : "") + ")";
				lAthlete.addAll(DatabaseHelper.execute("from Athlete where id in " + wId + " or link in " + wId + " order by id"));
				id = lAthlete.get(0).getId();
			}
			else
				lAthlete.add(e);
			Vector<String> vNm = new Vector<String>();
			Vector<Integer> vTm = new Vector<Integer>();
			Vector<Integer> vCn = new Vector<Integer>();
			Vector<Integer> vSp = new Vector<Integer>();
			StringBuffer sbNm = new StringBuffer();
			StringBuffer sbTm = new StringBuffer();
			StringBuffer sbCn = new StringBuffer();
			StringBuffer sbSp = new StringBuffer();
			ArrayList<Integer> lId = new ArrayList<Integer>();
			for (Athlete a : lAthlete) {
				lId.add(a.getId());
				if (StringUtils.notEmpty(a.getLastName())) {
					String fullName = null;
					if (a.getCountry() != null && a.getCountry().getCode().matches(StringUtils.PATTERN_REVERT_NAME))
						fullName = a.getLastName().toUpperCase() + (StringUtils.notEmpty(a.getFirstName()) ? " " + a.getFirstName() : "");
					else
						fullName = (StringUtils.notEmpty(a.getFirstName()) ? a.getFirstName() + " " : "") + a.getLastName().toUpperCase();
					if (!vNm.contains(fullName)) {
						sbNm.append(sbNm.toString().length() > 0 ? "<br/>" : "").append(fullName);
						vNm.add(fullName);
					}
				}
				if (a.getTeam() != null) {
					if (!vTm.contains(a.getTeam().getId())) {
						String s = HtmlUtils.writeLink(Team.alias, a.getTeam().getId(), a.getTeam().getLabel(), null);
						s = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, a.getTeam().getId(), ImageUtils.SIZE_SMALL, null, null), s);
						if (StringUtils.notEmpty(sbTm.toString()))
							s = s.replaceAll("\\<table\\>", "<table style='margin-top:2px;'>");
						sbTm.append(s);
						vTm.add(a.getTeam().getId());
					}
				}
				if (a.getCountry() != null) {
					if (!vCn.contains(a.getCountry().getId())) {
						String s = HtmlUtils.writeLink(Country.alias, a.getCountry().getId(), a.getCountry().getLabel(lang), null);
						s = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, a.getCountry().getId(), ImageUtils.SIZE_SMALL, null, null), s);
						if (StringUtils.notEmpty(sbCn.toString()))
							s = s.replaceAll("\\<table\\>", "<table style='margin-top:2px;'>");
						sbCn.append(s);
						vCn.add(a.getCountry().getId());
					}
				}
				if (a.getSport() != null) {
					if (!vSp.contains(a.getSport().getId())) {
						String s = HtmlUtils.writeLink(Sport.alias, a.getSport().getId(), a.getSport().getLabel(lang), null);
						s = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_SPORT, a.getSport().getId(), ImageUtils.SIZE_SMALL, null, null), s);
						if (StringUtils.notEmpty(sbSp.toString()))
							s = s.replaceAll("\\<table\\>", "<table style='margin-top:2px;'>");
						sbSp.append(s);
						vSp.add(a.getSport().getId());
					}
				}
			}
			String tm = sbTm.toString();
			String cn = sbCn.toString();
			String sp = sbSp.toString();
//			hInfo.put("tabtitle", e.getLastName() + ", " + e.getFirstName() + (e.getCountry() != null ? " (" + e.getCountry().getCode() + ")" : ""));
			hInfo.put("tabtitle", e.getFirstName() + " " + e.getLastName());
			hInfo.put("name", "<b>" + sbNm.toString() + "</b>");
			if (StringUtils.notEmpty(cn))
				hInfo.put("country", cn);
			if (StringUtils.notEmpty(sp))
				hInfo.put("sport", sp);
			if (StringUtils.notEmpty(tm))
				hInfo.put("team", tm);
			// Record
			ArrayList<Object> lFuncParams = new ArrayList<Object>();
			lFuncParams.add("PR");
			lFuncParams.add(e.getSport().getId());
			lFuncParams.add(lId.toString().replaceAll("\\[|\\]", "").replaceAll("\\,\\s", "-"));
			Collection<RefItem> cRecord = (Collection<RefItem>) DatabaseHelper.call("MedalCount", lFuncParams);
			if (!cRecord.isEmpty())
				hInfo.put("record", HtmlUtils.writeRecordItems(cRecord, lang));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(Championship.alias)) {
			Championship e = (Championship) DatabaseHelper.loadEntity(Championship.class, id);
			hInfo.put("tabtitle", e.getLabel(lang));
			hInfo.put("logo", HtmlUtils.writeImage(ImageUtils.INDEX_CHAMPIONSHIP, e.getId(), ImageUtils.SIZE_LARGE, null, null));
			hInfo.put("name", "<b>" + e.getLabel(lang).toUpperCase() + "</b>");
			hInfo.put("website", (e.getWebsite() != null ? "<a href='http://" + e.getWebsite() + "' target='_blank'>" + e.getWebsite() + "</a>" : StringUtils.EMPTY));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(City.alias)) {
			City e = (City) DatabaseHelper.loadEntity(City.class, id);
			String st = null;
			String cn = null;
			if (e.getState() != null) {
				st = HtmlUtils.writeLink(State.alias, e.getState().getId(), e.getState().getLabel(lang), null);
				st = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_STATE, e.getState().getId(), ImageUtils.SIZE_SMALL, null, null), st);
			}
			if (e.getCountry() != null) {
				cn = HtmlUtils.writeLink(Country.alias, e.getCountry().getId(), e.getCountry().getLabel(lang), null);
				cn = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, e.getCountry().getId(), ImageUtils.SIZE_SMALL, null, null), cn);
			}
			hInfo.put("tabtitle", e.getLabel(lang) + (e.getCountry() != null ? ", " + e.getCountry().getCode() : ""));
			hInfo.put("name", "<b>" + e.getLabel(lang).toUpperCase() + "</b>");
			hInfo.put("state", (st != null ? st : StringUtils.EMPTY));
			hInfo.put("country", (cn != null ? cn : StringUtils.EMPTY));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(Complex.alias)) {
			Complex e = (Complex) DatabaseHelper.loadEntity(Complex.class, id);
			String st = null;
			String cn = null;
			if (e.getCity().getState() != null) {
				st = HtmlUtils.writeLink(State.alias, e.getCity().getState().getId(), e.getCity().getState().getLabel(lang), null);
				st = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_STATE, e.getCity().getState().getId(), ImageUtils.SIZE_SMALL, null, null), st);
			}
			if (e.getCity().getCountry() != null) {
				cn = HtmlUtils.writeLink(Country.alias, e.getCity().getCountry().getId(), e.getCity().getCountry().getLabel(lang), null);
				cn = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, e.getCity().getCountry().getId(), ImageUtils.SIZE_SMALL, null, null), cn);
			}
			hInfo.put("tabtitle", e.getLabel(lang));
			hInfo.put("name", "<b>" + e.getLabel(lang).toUpperCase() + "</b>");
			hInfo.put("city", HtmlUtils.writeLink(City.alias, e.getCity().getId(), e.getCity().getLabel(lang), null));
			hInfo.put("state", (st != null ? st : StringUtils.EMPTY));
			hInfo.put("country", (cn != null ? cn : StringUtils.EMPTY));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(Country.alias)) {
			Country e = (Country) DatabaseHelper.loadEntity(Country.class, id);
			String currentLogo = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, e.getId(), ImageUtils.SIZE_LARGE, null, null);
			Collection<String> lAllLogos = ImageUtils.getImageList(ImageUtils.INDEX_COUNTRY, e.getId(), ImageUtils.SIZE_LARGE);
			hInfo.put("tabtitle", e.getLabel(lang) + " [" + e.getCode() + "]");
			hInfo.put("name", "<b>" + e.getLabel(lang).toUpperCase() + "</b>");
			hInfo.put("code", e.getCode());
			hInfo.put("flag", currentLogo);
			StringBuffer sbOtherFlags = new StringBuffer();
			if (lAllLogos != null && lAllLogos.size() > 1) {
				int nof = 0;
				for (String s : lAllLogos)
					if (s.matches(".*\\d{4}\\-\\d{4}\\.png$")) {
						String s_ = s.substring(s.indexOf("_") + 1, s.lastIndexOf("."));
						if (s_.substring(0, 4).equals(s_.substring(5)))
							s_ = s_.substring(5);
						sbOtherFlags.append("<div class='of-" + id + "' style='" + (++nof > 3 ? "display:none;" : "") + "float:left;margin:5px;text-align:center;'><img alt='' src='" + ImageUtils.getUrl() + s + "'/>");
						sbOtherFlags.append("<br/><b>" + s_ + "</b></div>");
					}
				if (nof > 3)
					sbOtherFlags.append("<div id='of-" + id + "-link' class='otherimglink' style='padding-top:90px;'><a href='javascript:moreImg(\"of-" + id + "\");'>" + ResourceUtils.getText("more", lang) + "</a></div>");
			}
			hInfo.put("otherflags", sbOtherFlags.toString());
			// Record
			ArrayList<Object> lFuncParams = new ArrayList<Object>();
			lFuncParams.add("CN");
			lFuncParams.add(0);
			lFuncParams.add(String.valueOf(e.getId()));
			Collection<RefItem> cRecord = (Collection<RefItem>) DatabaseHelper.call("MedalCount", lFuncParams);
			if (!cRecord.isEmpty())
				hInfo.put("record", HtmlUtils.writeRecordItems(cRecord, lang));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(Draw.alias)) {
			StringBuffer html = new StringBuffer();
			ArrayList<Object> lFuncParams = new ArrayList<Object>();
			lFuncParams.add(id);
			lFuncParams.add("_" + lang);
			List<DrawBean> lDraw = (List<DrawBean>) DatabaseHelper.call("GetDraw", lFuncParams);
			if (lDraw != null) {
				DrawBean bean = (DrawBean) lDraw.get(0);
				html.append("<span class='title'>" + bean.getYrLabel() + "&nbsp;" + ResourceUtils.getText("draw", lang) + "</span>");
				html.append("<div class='draw'>");
				String[] tLevel = {"Qf1", "Qf2", "Qf3", "Qf4", "Sf1", "Sf2", "F", "Thd"};
				HashMap<String, String> hLvlLabel = new HashMap<String, String>();
				hLvlLabel.put("Qf1", ResourceUtils.getText("quarterfinal", lang) + " #1");
				hLvlLabel.put("Qf2", ResourceUtils.getText("quarterfinal", lang) + " #2");
				hLvlLabel.put("Qf3", ResourceUtils.getText("quarterfinal", lang) + " #3");
				hLvlLabel.put("Qf4", ResourceUtils.getText("quarterfinal", lang) + " #4");
				hLvlLabel.put("Sf1", ResourceUtils.getText("semifinal", lang) + " #1");
				hLvlLabel.put("Sf2", ResourceUtils.getText("semifinal", lang) + " #2");
				hLvlLabel.put("F", ResourceUtils.getText("final", lang));
				hLvlLabel.put("Thd", ResourceUtils.getText("third.place", lang));
				for (String level : tLevel) {
					Method m1 = DrawBean.class.getMethod("getEn1" + level + "Str1");
					Method m2 = DrawBean.class.getMethod("getEn1" + level + "Str2");
					Method m3 = DrawBean.class.getMethod("getEn2" + level + "Str1");
					Method m4 = DrawBean.class.getMethod("getEn2" + level + "Str2");
					Method m5 = DrawBean.class.getMethod("getEn1" + level + "Id");
					Method m6 = DrawBean.class.getMethod("getEn2" + level + "Id");
					Method mRel1 = DrawBean.class.getMethod("getEn1" + level + "Rel1Id");
					Method mRel2 = DrawBean.class.getMethod("getEn1" + level + "Rel1Code");
					Method mRel3 = DrawBean.class.getMethod("getEn1" + level + "Rel1Label");
					Method mRel4 = DrawBean.class.getMethod("getEn1" + level + "Rel2Id");
					Method mRel5 = DrawBean.class.getMethod("getEn1" + level + "Rel2Code");
					Method mRel6 = DrawBean.class.getMethod("getEn1" + level + "Rel2Label");
					Method mRel7 = DrawBean.class.getMethod("getEn2" + level + "Rel1Id");
					Method mRel8 = DrawBean.class.getMethod("getEn2" + level + "Rel1Code");
					Method mRel9 = DrawBean.class.getMethod("getEn2" + level + "Rel1Label");
					Method mRel10 = DrawBean.class.getMethod("getEn2" + level + "Rel2Id");
					Method mRel11 = DrawBean.class.getMethod("getEn2" + level + "Rel2Code");
					Method mRel12 = DrawBean.class.getMethod("getEn2" + level + "Rel2Label");
					String e = getResultsEntity(bean.getDrType(), StringUtils.toInt(m5.invoke(bean)), String.valueOf(m1.invoke(bean)), String.valueOf(m2.invoke(bean)), bean.getYrLabel());
					if (e != null) {
						html.append("<div class='box " + level.toLowerCase() + "'><table><tr><th colspan='" + (bean.getDrType() < 10 ? 3 : 2) + "'>" + hLvlLabel.get(level) + "</th></tr>");
						String r = getResultsEntityRel(StringUtils.toInt(mRel1.invoke(bean)), String.valueOf(mRel2.invoke(bean)), String.valueOf(mRel3.invoke(bean)), StringUtils.toInt(mRel4.invoke(bean)), String.valueOf(mRel5.invoke(bean)), String.valueOf(mRel6.invoke(bean)), false, false, bean.getYrLabel());
						html.append("<tr><td style='font-weight:bold;'>" + e + "</td>" + (r != null ? r : ""));
						html.append("<td rowspan='2' style='width:33%;'>" + DrawBean.class.getMethod("get" + (level.equalsIgnoreCase("F") ? "Rs" : "Dr") + "Result" + level).invoke(bean) + "</td></tr>");
						e = getResultsEntity(bean.getDrType(), StringUtils.toInt(m6.invoke(bean)), String.valueOf(m3.invoke(bean)), String.valueOf(m4.invoke(bean)), bean.getYrLabel());
						r = getResultsEntityRel(StringUtils.toInt(mRel7.invoke(bean)), String.valueOf(mRel8.invoke(bean)), String.valueOf(mRel9.invoke(bean)), StringUtils.toInt(mRel10.invoke(bean)), String.valueOf(mRel11.invoke(bean)), String.valueOf(mRel12.invoke(bean)), false, false, bean.getYrLabel());
						html.append("<tr><td>" + e + "</td>" + (r != null ? r : "") + "</tr>");
						html.append("</table></div>");
					}
				}
				html.append("</div>");
			}
			return html;
		}
		else if (type.equals(Event.alias)) {
			Event e = (Event) DatabaseHelper.loadEntity(Event.class, id);
			hInfo.put("tabtitle", e.getLabel(lang));
			hInfo.put("name", "<b>" + e.getLabel(lang).toUpperCase() + "</b>");
			hInfo.put("website", (e.getWebsite() != null ? "<a href='http://" + e.getWebsite() + "' target='_blank'>" + e.getWebsite() + "</a>" : StringUtils.EMPTY));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(Olympics.alias)) {
			Olympics e = (Olympics) DatabaseHelper.loadEntity(Olympics.class, id);
			String st = null;
			String cn = null;
			if (e.getCity().getState() != null) {
				st = HtmlUtils.writeLink(State.alias, e.getCity().getState().getId(), e.getCity().getState().getLabel(lang), null);
				st = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_STATE, e.getCity().getState().getId(), ImageUtils.SIZE_SMALL, null, null), st);
			}
			if (e.getCity().getCountry() != null) {
				cn = HtmlUtils.writeLink(Country.alias, e.getCity().getCountry().getId(), e.getCity().getCountry().getLabel(lang), null);
				cn = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, e.getCity().getCountry().getId(), ImageUtils.SIZE_SMALL, null, null), cn);
			}
			hInfo.put("tabtitle", e.getCity().getLabel(lang) + "&nbsp;" + e.getYear().getLabel());
			hInfo.put("logo", HtmlUtils.writeImage(ImageUtils.INDEX_OLYMPICS, e.getId(), ImageUtils.SIZE_LARGE, null, null));
//			hInfo.put("type", ResourceUtils.getText(e.getType() == 0 ? "summer" : "winter", lang) + " " + ResourceUtils.getText("olympic.games", lang));
			hInfo.put("year", HtmlUtils.writeLink(Year.alias, e.getYear().getId(), e.getYear().getLabel(), null));
			hInfo.put("city", HtmlUtils.writeLink(City.alias, e.getCity().getId(), e.getCity().getLabel(lang), null));
			hInfo.put("country", (cn != null ? cn : StringUtils.EMPTY));
			hInfo.put("start.date", e.getDate1());
			hInfo.put("end.date", e.getDate2());
			hInfo.put("sports", String.valueOf(e.getCountSport()));
			hInfo.put("events", String.valueOf(e.getCountEvent()));
			hInfo.put("countries", String.valueOf(e.getCountCountry()));
			hInfo.put("athletes", String.valueOf(e.getCountPerson()));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(Sport.alias)) {
			Sport e = (Sport) DatabaseHelper.loadEntity(Sport.class, id);
			hInfo.put("_sport_", "1");
			hInfo.put("width", "280");
			hInfo.put("tabtitle", e.getLabel(lang));
			hInfo.put("logo", HtmlUtils.writeImage(ImageUtils.INDEX_SPORT, e.getId(), ImageUtils.SIZE_LARGE, null, null));
			hInfo.put("titlename", e.getLabel(lang).toUpperCase());
			hInfo.put("website", (e.getWebsite() != null ? "<a href='http://" + e.getWebsite() + "' target='_blank'>" + e.getWebsite() + "</a>" : StringUtils.EMPTY));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
			StringWriter sw = new StringWriter();
			ArrayList<Object> lFuncParams = new ArrayList<Object>();
			lFuncParams.add("WHERE SP.id=" + id);
			lFuncParams.add("_" + lang);
			HtmlConverter.convertTreeArray(DatabaseHelper.call("TreeResults", lFuncParams), sw, true);
			hInfo.put("tree", "<div class='treediv'><div id='treeview' class='collapsed'><table cellpadding='0' cellspacing='0'><tr><td><script type='text/javascript'>var " + sw.toString() + "new Tree(treeItems, treeTemplate);</script></td></tr></table></div></div>");
		}
		else if (type.equals(State.alias)) {
			State e = (State) DatabaseHelper.loadEntity(State.class, id);
			hInfo.put("tabtitle", e.getLabel(lang));
			hInfo.put("flag", HtmlUtils.writeImage(ImageUtils.INDEX_STATE, e.getId(), ImageUtils.SIZE_LARGE, null, null));
			hInfo.put("name", "<b>" + e.getLabel(lang).toUpperCase() + "</b>");
			hInfo.put("code", e.getCode());
			hInfo.put("capital", e.getCapital());
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(Team.alias)) {
			int currentId = id;
			LinkedList<Team> lTeam = new LinkedList<Team>();
			Team e = (Team) DatabaseHelper.loadEntity(Team.class, id);
			if (e.getLink() != null && e.getLink() >= 0) {
				Team e_ = (Team) DatabaseHelper.loadEntity(Team.class, e.getLink());
				String wId = "(-1" + (e_ != null && e_.getId() > 0 ? "," + e_.getId() : "") + (e_ != null && e_.getLink() > 0 ? "," + e_.getLink() : "") + (e.getId() > 0 ? "," + e.getId() : "") + (e.getLink() > 0 ? "," + e.getLink() : "") + ")";
				lTeam.addAll(DatabaseHelper.execute("from Team where id in " + wId + " or link in " + wId + " order by year1 desc"));
				id = lTeam.get(0).getId();
			}
			else
				lTeam.add(e);
			String currentLogo = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, e.getId(), ImageUtils.SIZE_LARGE, null, null);
			Collection<String> lAllLogos = ImageUtils.getImageList(ImageUtils.INDEX_TEAM, e.getId(), ImageUtils.SIZE_LARGE);
			ArrayList<Integer> lId = new ArrayList<Integer>();
			StringBuffer sbTm = new StringBuffer();
			for (Team t : lTeam) {
				lId.add(t.getId());
				sbTm.append((t.getInactive() != null && t.getInactive() ? "&dagger;&nbsp;" : "") + (t.getId() == currentId ? "<b>" : "") + HtmlUtils.writeLink(Team.alias, t.getId(), t.getLabel(), null) + " (" + t.getYear1() + "&nbsp;-&nbsp;" + (StringUtils.notEmpty(t.getYear2()) ? t.getYear2() : ResourceUtils.getText("today", lang)) + ")" + (t.getId() == currentId ? "</b>" : "") + "<br/>");				
			}
			
			String tm = sbTm.toString();
			String cn = null;
			if (e.getCountry() != null) {
				cn = HtmlUtils.writeLink(Country.alias, e.getCountry().getId(), e.getCountry().getLabel(lang), null);
				cn = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, e.getCountry().getId(), ImageUtils.SIZE_SMALL, null, null), cn);
			}
			String sp = null;
			if (e.getSport() != null) {
				sp = HtmlUtils.writeLink(Sport.alias, e.getSport().getId(), e.getSport().getLabel(lang), null);
				sp = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_SPORT, e.getSport().getId(), ImageUtils.SIZE_SMALL, null, null), sp);
			}
			hInfo.put("tabtitle", e.getLabel());
			hInfo.put("name", (e.getInactive() != null && e.getInactive() ? "&dagger;&nbsp;" : "") + "<b>" + e.getLabel().toUpperCase() + "</b>");
			hInfo.put("logo", currentLogo);
			StringBuffer sbOtherLogos = new StringBuffer();
			if (lAllLogos != null && lAllLogos.size() > 1) {
				int nol = 0;
				for (String s : lAllLogos)
					if (s.matches(".*\\d{4}\\-\\d{4}\\.png$")) {
						String s_ = s.substring(s.indexOf("_") + 1, s.lastIndexOf("."));
						if (s_.substring(0, 4).equals(s_.substring(5)))
							s_ = s_.substring(5);
						sbOtherLogos.append("<div class='ol-" + id + "' style='" + (++nol > 3 ? "display:none;" : "") + "float:left;margin:5px;text-align:center;'><img alt='' src='" + ImageUtils.getUrl() + s + "'/>");
						sbOtherLogos.append("<br/><b>" + s_ + "</b></div>");
					}
				if (nol > 3)
					sbOtherLogos.append("<div id='ol-" + id + "-link' class='otherimglink' style='padding-top:110px;'><a href='javascript:moreImg(\"ol-" + id + "\");'>" + ResourceUtils.getText("more", lang) + "</a></div>");
			}
			hInfo.put("otherlogos", sbOtherLogos.toString());
			if (cn != null)
				hInfo.put("country", cn);
			if (sp != null)
				hInfo.put("sport", sp);
			if (StringUtils.notEmpty(e.getConference()))
				hInfo.put("conference", e.getConference());
			if (StringUtils.notEmpty(e.getDivision()))
				hInfo.put("division", e.getDivision());
			if (lTeam.size() > 1 || StringUtils.notEmpty(e.getYear1()))
				hInfo.put("franchist", tm);
			// Record
			ArrayList<Object> lFuncParams = new ArrayList<Object>();
			lFuncParams.add("TM");
			lFuncParams.add(e.getSport().getId());
			lFuncParams.add(lId.toString().replaceAll("\\[|\\]", "").replaceAll("\\,\\s", "-"));
			Collection<RefItem> cRecord = (Collection<RefItem>) DatabaseHelper.call("MedalCount", lFuncParams);
			if (!cRecord.isEmpty())
				hInfo.put("record", HtmlUtils.writeRecordItems(cRecord, lang));
			hInfo.put("extlinks", HtmlUtils.writeExternalLinks(e));
		}
		else if (type.equals(Year.alias)) {
			Year e = (Year) DatabaseHelper.loadEntity(Year.class, id);
			hInfo.put("tabtitle", e.getLabel());
			hInfo.put("name", e.getLabel());
		}
		hInfo.put("title", ResourceUtils.getText("entity." + type + ".1", lang) + " #" + id);
		return HtmlUtils.writeInfoHeader(hInfo, lang);
	}

	public static StringBuffer getRecordRef(ArrayList<Object> params, Collection<Object> coll, boolean isExport, String lang) throws Exception {
		String type = String.valueOf(params.get(0));
		boolean isAllRef = !StringUtils.notEmpty(params.get(2));
		final int ITEM_LIMIT = 50;
		StringBuffer html = new StringBuffer();
		if (isAllRef)
			html.append("<table class='tsort'>");
		HashMap<String, Integer> hCount = new HashMap<String, Integer>();
		// Evaluate items
		for (Object obj : coll) {
			String s = ((RefItem) obj).getEntity();
			if (!hCount.containsKey(s))
				hCount.put(s, 0);
			hCount.put(s, isAllRef && hCount.get(s) + 1 > ITEM_LIMIT ? ITEM_LIMIT : hCount.get(s) + 1);
		}
		// Resort (results/draws)
		ArrayList<Object> list = new ArrayList<Object>(coll);
		for (int i = 0 ; i < list.size() ; i++) {
			RefItem item = (RefItem) list.get(i);
			String en = item.getEntity();
			boolean isDraw = (en != null && en.equals(Result.alias) && item.getTxt2() != null && item.getTxt2().matches("(qf|sf|th)(1|2|3|4|d)"));
			if (isDraw) {
				for (int j = i ; j < list.size() ; j++) {
					RefItem item_ = (RefItem) list.get(j);
					String en_ = item.getEntity();
					boolean isDraw_ = (en_ != null && en_.equals(Result.alias) && item_.getTxt2() != null && item_.getTxt2().matches("(qf|sf|th)(1|2|3|4|d)"));
					if (j < list.size() - 2 && !isDraw_ && item_.getIdRel1() < item.getIdRel1()) {
						list.add(j, item);
						break;
					}
				}
				list.remove(i);
				i--;
			}
			else
				break;
		}
		// Write items
		String currentEntity = "";
		int colspan = 0;
		int count = 0;
		final String HTML_SEE_FULL = "<tr class='refseefull' onclick='refSeeFull(this, \"#PARAMS#\");'><td colspan='#COLSPAN#'></td></tr>";
		String c1 = null, c2 = null, c3 = null, c4 = null, c5 = null, c6 = null, c7 = null;
		for (Object obj : list) {
			RefItem item = (RefItem) obj;
			String en = item.getEntity();
			if (!en.equals(currentEntity)) {
				String id = en + "-" + System.currentTimeMillis();
				StringBuffer cols = new StringBuffer();
				if (en.equals(Athlete.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("name", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("sport", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("country", lang) + "</th>");
				if (en.equals(City.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("name", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("country", lang) + "</th>");
				else if (en.equals(Complex.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("name", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("city", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("country", lang) + "</th>");
				else if (en.equals(HallOfFame.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("league", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>Year</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("name", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("inducted.as", lang) + "</th>");
				else if (en.equals(Olympics.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'></th><th onclick='sort(\"" + id + "\", this, 1);'>Type</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("year", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>City</th><th onclick='sort(\"" + id + "\", this, 4);'>" + ResourceUtils.getText("country", lang) + "</th>");
				else if (en.equals(OlympicRanking.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("entity.OL", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("country", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ImageUtils.getGoldHeader(lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ImageUtils.getSilverHeader(lang) + "</th><th onclick='sort(\"" + id + "\", this, 4);'>" + ImageUtils.getBronzeHeader(lang) + "</th>");
				else if (en.equals(Record.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("sport", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("category", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>Type</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("record", lang) + "</th>" + (type.matches("PR|TM|CN") ? "<th onclick='sort(\"" + id + "\", this, 4);'>" + ResourceUtils.getText("rank", lang) + "</th>" : ""));
				else if (en.equals(Result.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("sport", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("event", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("year", lang)  + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("entity.RS.1", lang) + "</th>");
				else if (en.equals(RetiredNumber.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("league", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("team", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("name", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("number", lang) + "</th>");
				else if (en.equals(Team.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("name", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("country", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("sport", lang) + "</th>");
				else if (en.equals(TeamStadium.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("league", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("team", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("complex", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("city", lang) + "</th><th onclick='sort(\"" + id + "\", this, 4);'>" + ResourceUtils.getText("state", lang) + "</th><th onclick='sort(\"" + id + "\", this, 5);'>Country</th><th onclick='sort(\"" + id + "\", this, 6);'>" + ResourceUtils.getText("timespan", lang) + "</th>");
				else if (en.equals(WinLoss.alias))
					cols.append("<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("league", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("team", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("type", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("w.l", lang) + "</th>");
				if (isAllRef && count >= ITEM_LIMIT)
					html.append(HTML_SEE_FULL.replaceAll("#PARAMS#", StringUtils.encode(params.get(0) + "-" + params.get(1) + "-" + currentEntity)).replaceAll("#COLSPAN#", String.valueOf(colspan)));
				colspan = StringUtils.countIn(cols.toString(), "<th");
				html.append(StringUtils.notEmpty(currentEntity) ? "</tbody></table><table class='tsort'>" : "");
				count = 0;
				html.append("<thead><tr><th colspan='" + colspan + "'>" + HtmlUtils.writeToggleTitle(ResourceUtils.getText("entity." + en, lang) + "&nbsp;(" + hCount.get(en) + ")") + "</th></tr>");
				html.append("<tr class='rsort'>" + cols.toString() + "</tr></thead><tbody id='tb-" + id + "'>");
				currentEntity = en;
			}
			c1 = null;
			c2 = null;
			c3 = null;
			c4 = null;
			c5 = null;
			c6 = null;
			c7 = null;
			HashMap<String, Integer> hLeague = new HashMap<String, Integer>();
			hLeague.put("NFL", 51);
			hLeague.put("NBA", 54);
			hLeague.put("NHL", 55);
			hLeague.put("MLB", 56);
			if (en.equals(Athlete.alias)) {
				c1 = HtmlUtils.writeLink(Athlete.alias, item.getIdItem(), item.getLabel(), null);
				c2 = HtmlUtils.writeLink(Sport.alias, item.getIdRel2(), item.getLabelRel2(), null);
//				c3 = (item.getIdRel3() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, item.getIdRel3(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Team.alias, item.getIdRel3(), item.getLabelRel3(), null)) : StringUtils.EMPTY);
				c4 = (item.getIdRel1() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel1(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Country.alias, item.getIdRel1(), item.getLabelRel1(), null)) : StringUtils.EMPTY);
			}
			else if (en.equals(City.alias)) {
				c1 = HtmlUtils.writeLink(City.alias, item.getIdItem(), item.getLabel(), null);
//				c2 = (item.getIdRel1() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_STATE, item.getIdRel1(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(State.alias, item.getIdRel1(), item.getLabelRel1())) : StringUtils.EMPTY);
				c3 = (item.getIdRel2() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel2(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Country.alias, item.getIdRel2(), item.getLabelRel2(), null)) : StringUtils.EMPTY);
			}
			else if (en.equals(Complex.alias)) {
				c1 = HtmlUtils.writeLink(Complex.alias, item.getIdItem(), item.getLabel(), null);
				c2 = HtmlUtils.writeLink(City.alias, item.getIdRel1(), item.getLabelRel1(), null);
//				c3 = (item.getIdRel2() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_STATE, item.getIdRel2(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(State.alias, item.getIdRel2(), item.getLabelRel2())) : StringUtils.EMPTY);
				c4 = (item.getIdRel3() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel3(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Country.alias, item.getIdRel3(), item.getLabelRel3(), null)) : StringUtils.EMPTY);
			}
			else if (en.equals(HallOfFame.alias)) {
				int l = hLeague.get(item.getComment());
				c1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_CHAMPIONSHIP, l, ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Championship.alias, l, item.getComment(), null));
				c2 = HtmlUtils.writeLink(Year.alias, item.getIdRel1(), item.getLabelRel1(), null);
				c3 = HtmlUtils.writeLink(Athlete.alias, item.getIdRel2(), item.getLabelRel2(), null);
				c4 = "-";
				if (StringUtils.notEmpty(item.getTxt1())) {
					StringBuffer sbPos = new StringBuffer();
					for (String s : item.getTxt1().split("\\-"))
						sbPos.append((!sbPos.toString().isEmpty() ? "&nbsp;/&nbsp;" : "") + StringUtils.getUSPosition(item.getIdRel3(), s));
					c4 = sbPos.toString();
				}
			}
			else if (en.equals(Olympics.alias)) {
				c1 = HtmlUtils.writeLink(Olympics.alias, item.getIdItem(), HtmlUtils.writeImage(ImageUtils.INDEX_OLYMPICS, item.getIdItem(), ImageUtils.SIZE_SMALL, null, null), null);
				c2 = ResourceUtils.getText("olympic.games", lang) + " (" + (item.getComment().equals("0") ? ResourceUtils.getText("summer", lang) : ResourceUtils.getText("winter", lang)) + ")";
				c3 = HtmlUtils.writeLink(Year.alias, item.getIdRel1(), item.getLabelRel1(), null);
				c4 = HtmlUtils.writeLink(City.alias, item.getIdRel2(), item.getLabelRel2(), null);
//				c5 = (item.getIdRel3() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_STATE, item.getIdRel3(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(State.alias, item.getIdRel3(), item.getLabelRel3())) : StringUtils.EMPTY);
				c6 = (item.getIdRel4() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel4(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), HtmlUtils.writeLink(Country.alias, item.getIdRel4(), item.getLabelRel4(), null)) : StringUtils.EMPTY);
			}
			else if (en.equals(OlympicRanking.alias)) {
				String[] t = item.getComment().split(",");
				c1 = HtmlUtils.writeLink(Year.alias, item.getIdRel2(), item.getLabelRel2(), null) + "&nbsp;" + HtmlUtils.writeLink(City.alias, item.getIdRel3(), item.getLabelRel3(), null);
				c2 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel4(), ImageUtils.SIZE_SMALL, item.getLabelRel2(), null), HtmlUtils.writeLink(Country.alias, item.getIdRel4(), item.getLabelRel4(), null));
				c3 = t[0];
				c4 = t[1];
				c5 = t[2];
			}
			else if (en.equals(Record.alias)) {
				c1 = HtmlUtils.writeLink(Sport.alias, item.getIdRel1(), item.getLabelRel1(), null);
				c2 = (item.getIdRel2() != null ? HtmlUtils.writeLink(Championship.alias, item.getIdRel2(), item.getLabelRel2(), null) + " - " + HtmlUtils.writeLink(Event.alias, item.getIdRel3(), item.getLabelRel3() + (item.getIdRel4() != null ? " - " + HtmlUtils.writeLink(Event.alias, item.getIdRel4(), item.getLabelRel4(), null) : ""), null) : StringUtils.EMPTY);
				c3 = (item.getTxt1() != null ? item.getTxt1() + " - " + item.getTxt2() : StringUtils.EMPTY);
				c4 = item.getLabel();
				c5 = (item.getComment() != null ? ResourceUtils.getText("rank." + (item.getComment().equals("1") ? "rec." : "") + item.getComment(), lang) : null);
			}
			else if (en.equals(Result.alias)) {
				c1 = HtmlUtils.writeLink(Sport.alias, item.getIdRel2(), item.getLabelRel2(), null);
				c2 = HtmlUtils.writeLink(Result.alias, item.getIdItem(), item.getLabelRel3() + "&nbsp;-&nbsp;" + item.getLabelRel4() + (item.getIdRel5() != null ? "&nbsp;-&nbsp;" + item.getLabelRel5() : "") + (item.getIdRel18() != null ? "&nbsp;-&nbsp;" + item.getLabelRel18() : ""), null);
				c3 = HtmlUtils.writeLink(Year.alias, item.getIdRel1(), item.getLabelRel1(), null);
				if (item.getIdRel6() != null && item.getTxt2() != null && item.getTxt2().matches("(qf|sf|th)(1|2|3|4|d)")) { // Draw
					String s = item.getLabelRel6().toLowerCase();
					String alias = (s.matches(StringUtils.PATTERN_COUNTRY) ? Country.alias : (s.matches(StringUtils.PATTERN_ATHLETE) ? Athlete.alias : Team.alias));
					String[] tEntity = new String[2];
					tEntity[0] = (item.getIdRel6() != null ? HtmlUtils.writeLink(alias, item.getIdRel6(), StringUtils.getShortName(item.getLabelRel6()), null) : null);
					tEntity[1] = (item.getIdRel7() != null ? HtmlUtils.writeLink(alias, item.getIdRel7(), StringUtils.getShortName(item.getLabelRel7()), null) : null);
					short index = (alias.equals(Athlete.alias) || alias.equals(Country.alias) ? ImageUtils.INDEX_COUNTRY : ImageUtils.INDEX_TEAM);
					tEntity[0] = (tEntity[0] != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(index, alias.equals(Athlete.alias) && item.getIdRel12() != null ? item.getIdRel12() : item.getIdRel6(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), tEntity[0]) : null);
					tEntity[1] = (tEntity[1] != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(index, alias.equals(Athlete.alias) && item.getIdRel13() != null ? item.getIdRel13() : item.getIdRel7(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), tEntity[1]) : null);
					StringBuffer sb = new StringBuffer("<table><tr>");
					sb.append("<table><tr><td>" + ResourceUtils.getText("draw." + item.getTxt2().replaceAll("\\d$", ""), lang) + "&nbsp;:&nbsp;</td><td><b>" + tEntity[0] + "</b></td><td>&nbsp;-&nbsp;</td><td>" + tEntity[1] + "</td><td>&nbsp;" + item.getTxt1());
					c4 = sb.append("</tr></table>").toString();
				}
				else if (item.getIdRel6() != null && item.getLabelRel6() != null) { // Result
					String s = item.getLabelRel6().toLowerCase();
					String alias = (s.matches(StringUtils.PATTERN_COUNTRY) ? Country.alias : (s.matches(StringUtils.PATTERN_ATHLETE) ? Athlete.alias : Team.alias));
					boolean isMedal = String.valueOf(item.getIdRel3()).matches("1|3|4");
					String[] tEntity = new String[6];
					tEntity[0] = (item.getIdRel6() != null ? HtmlUtils.writeLink(alias, item.getIdRel6(), StringUtils.getShortName(item.getLabelRel6()), null) : null);
					tEntity[1] = (item.getIdRel7() != null ? HtmlUtils.writeLink(alias, item.getIdRel7(), StringUtils.getShortName(item.getLabelRel7()), null) : null);
					tEntity[2] = (item.getIdRel8() != null ? HtmlUtils.writeLink(alias, item.getIdRel8(), StringUtils.getShortName(item.getLabelRel8()), null) : null);
					tEntity[3] = (item.getIdRel9() != null ? HtmlUtils.writeLink(alias, item.getIdRel9(), StringUtils.getShortName(item.getLabelRel9()), null) : null);
					tEntity[4] = (item.getIdRel10() != null ? HtmlUtils.writeLink(alias, item.getIdRel10(), StringUtils.getShortName(item.getLabelRel10()), null) : null);
					tEntity[5] = (item.getIdRel11() != null ? HtmlUtils.writeLink(alias, item.getIdRel11(), StringUtils.getShortName(item.getLabelRel11()), null) : null);
					short index = (alias.equals(Athlete.alias) || alias.equals(Country.alias) ? ImageUtils.INDEX_COUNTRY : ImageUtils.INDEX_TEAM);
					tEntity[0] = (tEntity[0] != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(index, alias.equals(Athlete.alias) && item.getIdRel12() != null ? item.getIdRel12() : item.getIdRel6(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), tEntity[0]) : null);
					tEntity[1] = (tEntity[1] != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(index, alias.equals(Athlete.alias) && item.getIdRel13() != null ? item.getIdRel13() : item.getIdRel7(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), tEntity[1]) : null);
					tEntity[2] = (tEntity[2] != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(index, alias.equals(Athlete.alias) && item.getIdRel14() != null ? item.getIdRel14() : item.getIdRel8(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), tEntity[2]) : null);
					tEntity[3] = (tEntity[3] != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(index, alias.equals(Athlete.alias) && item.getIdRel15() != null ? item.getIdRel15() : item.getIdRel9(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), tEntity[3]) : null);
					tEntity[4] = (tEntity[4] != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(index, alias.equals(Athlete.alias) && item.getIdRel16() != null ? item.getIdRel16() : item.getIdRel10(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), tEntity[4]) : null);
					tEntity[5] = (tEntity[5] != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(index, alias.equals(Athlete.alias) && item.getIdRel17() != null ? item.getIdRel17() : item.getIdRel11(), ImageUtils.SIZE_SMALL, item.getLabelRel1(), null), tEntity[5]) : null);
					List<Integer> listEq = StringUtils.tieList(item.getTxt4());
					if (listEq != null && !listEq.isEmpty()) {
						boolean isFirst = true;
						Integer idx = listEq.get(0) - 1;
						for (int i = 1 ; i < listEq.size() ; i++) {
							if (idx == null)
								idx = listEq.get(i) - 1;
							else {
								if (listEq.get(i) == -1)
									idx = null;
								if (idx == null || listEq.get(0) > 1)
									isFirst = false;
								if (idx != null) {
									if (idx < tEntity.length && tEntity[idx] != null && (listEq.get(i) - 1) < tEntity.length && tEntity[listEq.get(i) - 1] != null) {
										tEntity[idx] = tEntity[idx].concat((isFirst ? "</b>" : "") + "</td><td>" + (isFirst ? "<b>" : "") + "&nbsp;/&nbsp;" + (isFirst ? "</b>" : "") + "</td><td>" + (isFirst ? "<b>" : "") + tEntity[listEq.get(i) - 1]);
										tEntity[listEq.get(i) - 1] = null;
									}
								}
							}
						}
						tEntity = StringUtils.removeNulls(tEntity);
					}
					boolean isScore = (tEntity[0] != null && tEntity[1] != null && StringUtils.notEmpty(item.getTxt1()) && !StringUtils.notEmpty(item.getTxt2()));
					//isMedal = false;
					if (isScore && !isMedal) {
						StringBuffer sb = new StringBuffer("<table><tr>");
						sb.append("<table><tr><td>" + ResourceUtils.getText("draw.f", lang) + "&nbsp;:&nbsp;</td><td><b>" + tEntity[0] + "</b></td><td>&nbsp;-&nbsp;</td><td>" + tEntity[1] + "</td><td>&nbsp;" + item.getTxt1());
						c4 = sb.append("</tr></table>").toString();
					}
					else {
						StringBuffer sb = new StringBuffer("<table><tr>");
						sb.append("<td>" + (tEntity[0] != null || isMedal ? (isMedal ? ImageUtils.getGoldMedImg() : "1.") + "&nbsp;" : "") + "</td><td><b>" + tEntity[0] + "</b></td>");
						if (tEntity[1] != null)
							sb.append("<td>&nbsp;" + (isMedal ? ImageUtils.getSilverMedImg() : "2.") + "&nbsp;</td><td>" + tEntity[1] + "</td>");
						if (tEntity[2] != null)
							sb.append("<td>&nbsp;" + (isMedal ? ImageUtils.getBronzeMedImg() : "3.") + "&nbsp;</td><td>" + tEntity[2] + "</td>");
						c4 = sb.append("</tr></table>").toString();
					}
				}
			}
			else if (en.equals(RetiredNumber.alias)) {
				int l = hLeague.get(item.getComment());
				c1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_CHAMPIONSHIP, l, ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Championship.alias, l, item.getComment(), null));
				c2 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, item.getIdRel1(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Team.alias, item.getIdRel1(), item.getLabelRel1(), null));
				c3 = HtmlUtils.writeLink(Athlete.alias, item.getIdRel2(), item.getLabelRel2(), null);
				c4 = String.valueOf(item.getIdRel3());
			}
			else if (en.equals(Team.alias)) {
				c1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, item.getIdItem(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Team.alias, item.getIdItem(), item.getLabel(), null));
				c2 = (item.getIdRel1() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel1(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Country.alias, item.getIdRel1(), item.getLabelRel1(), null)) : StringUtils.EMPTY);
				c3 = HtmlUtils.writeLink(Sport.alias, item.getIdRel2(), item.getLabelRel2(), null);
			}
			else if (en.equals(TeamStadium.alias)) {
				int l = hLeague.get(item.getComment());
				c1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_CHAMPIONSHIP, l, ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Championship.alias, l, item.getComment(), null));
				c2 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, item.getIdRel1(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Team.alias, item.getIdRel1(), item.getLabelRel1(), null));
				c3 = HtmlUtils.writeLink(Complex.alias, item.getIdRel2(), item.getLabelRel2(), null);
				c4 = HtmlUtils.writeLink(City.alias, item.getIdRel3(), item.getLabelRel3(), null);
				c5 = (item.getIdRel4() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_STATE, item.getIdRel4(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(State.alias, item.getIdRel4(), item.getLabelRel4(), null)) : StringUtils.EMPTY);
				c6 = (item.getIdRel5() != null ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel5(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Country.alias, item.getIdRel5(), item.getLabelRel5(), null)) : StringUtils.EMPTY);
				c7 = item.getTxt1() + "&nbsp;-&nbsp;" + (StringUtils.notEmpty(item.getTxt2()) && !item.getTxt2().equals("0") ? item.getTxt2() : ResourceUtils.getText("today", lang));
			}
			else if (en.equals(WinLoss.alias)) {
				int l = hLeague.get(item.getComment());
				c1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_CHAMPIONSHIP, l, ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Championship.alias, l, item.getComment(), null));
				c2 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, item.getIdRel1(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Team.alias, item.getIdRel1(), item.getLabelRel1(), null));
				c3 = item.getTxt1();
				c4 = item.getTxt2();
			}
			if (isExport || !isAllRef || count++ < ITEM_LIMIT) {
				html.append("<tr>" + (c1 != null ? "<td class='srt'>" + c1 + "</td>" : "") + (c2 != null ? "<td class='srt'>" + c2 + "</td>" : ""));
				html.append((c3 != null ? "<td class='srt'>" + c3 + "</td>" : "") + (c4 != null ? "<td class='srt'>" + c4 + "</td>" : ""));
				html.append((c5 != null ? "<td class='srt'>" + c5 + "</td>" : "") + (c6 != null ? "<td class='srt'>" + c6 + "</td>" : ""));
				html.append((c7 != null ? "<td class='srt'>" + c7 + "</td>" : "") + "</tr>");
			}
		}
		if (isAllRef && count >= ITEM_LIMIT)
			html.append(HTML_SEE_FULL.replaceAll("#PARAMS#", StringUtils.encode(params.get(0) + "-" + params.get(1) + "-" + currentEntity)).replaceAll("#COLSPAN#", String.valueOf(colspan)));
		html.append(isAllRef ? "</tbody></table>" : "");
		return html;
	}

	public static StringBuffer convertResults(Collection<Object> coll, Championship cp, Event ev, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		// Evaluate columns
		short entityCount = 0;
		short[] tColspan = {1, 1, 1, 1, 1, 1, 1, 1, 1};
		boolean[] tIsEntityRel1 = {false, false, false, false, false, false, false, false, false};
		boolean[] tIsEntityRel2 = {false, false, false, false, false, false, false, false, false};
		boolean[] tIsResult = {false, false, false, false, false, false, false, false, false};
		boolean isDates = false;
		boolean isPlace = false;
		boolean isDraw = false;
		boolean isComment = false;
		int type = ev.getType().getNumber();
		boolean isDoubles = (type == 4);
		boolean isMedal = String.valueOf(cp.getId()).matches("1|3|4");
		ArrayList<Integer> listEqDoubles = new ArrayList<Integer>();
		listEqDoubles.add(1);listEqDoubles.add(2);listEqDoubles.add(-1);
		listEqDoubles.add(3);listEqDoubles.add(4);listEqDoubles.add(-1);
		listEqDoubles.add(5);listEqDoubles.add(6);listEqDoubles.add(-1);
		listEqDoubles.add(7);listEqDoubles.add(8);
		for (Object obj : coll) {
			ResultsBean bean = (ResultsBean) obj;
			List<Integer> listEq = (isDoubles ? new ArrayList<Integer>(listEqDoubles) : StringUtils.tieList(bean.getRsExa()));
			if (isDoubles && StringUtils.notEmpty(bean.getRsExa()) && bean.getRsExa().equals("5-8"))
				listEq.remove(listEq.lastIndexOf(-1));
			String sListEq = listEq.toString();
			entityCount = (entityCount < 1 && bean.getRsRank1() != null ? 1 : entityCount);
			entityCount = (entityCount < 2 && bean.getRsRank2() != null && (listEq.indexOf(2) <= 0 || StringUtils.countIn(sListEq, "-1") >= 1) ? 2 : entityCount);
			entityCount = (entityCount < 3 && bean.getRsRank3() != null && (listEq.indexOf(3) <= 0 || StringUtils.countIn(sListEq, "-1") >= 2) ? 3 : entityCount);
			entityCount = (entityCount < 4 && bean.getRsRank4() != null && (listEq.indexOf(4) <= 0 || StringUtils.countIn(sListEq, "-1") >= 3) ? 4 : entityCount);
			entityCount = (entityCount < 5 && bean.getRsRank5() != null && (listEq.indexOf(5) <= 0 || StringUtils.countIn(sListEq, "-1") >= 4) ? 5 : entityCount);
			entityCount = (entityCount < 6 && bean.getRsRank6() != null && (listEq.indexOf(6) <= 0 || StringUtils.countIn(sListEq, "-1") >= 5) ? 6 : entityCount);
			tColspan[0] = (tColspan[0] < 2 && StringUtils.notEmpty(bean.getRsResult1()) ? 2 : tColspan[0]);
			tColspan[1] = (tColspan[1] < 2 && StringUtils.notEmpty(bean.getRsResult2()) ? 2 : tColspan[1]);
			tColspan[2] = (tColspan[2] < 2 && StringUtils.notEmpty(bean.getRsResult3()) ? 2 : tColspan[2]);
			tColspan[3] = (tColspan[3] < 2 && StringUtils.notEmpty(bean.getRsResult4()) ? 2 : tColspan[3]);
			tColspan[4] = (tColspan[4] < 2 && StringUtils.notEmpty(bean.getRsResult5()) ? 2 : tColspan[4]);
			tIsEntityRel1[0] |= (bean.getEn1Rel1Id() != null);tIsEntityRel2[0] |= (bean.getEn1Rel2Id() != null);
			tIsEntityRel1[1] |= (bean.getEn2Rel1Id() != null);tIsEntityRel2[1] |= (bean.getEn2Rel2Id() != null);
			tIsEntityRel1[2] |= (bean.getEn3Rel1Id() != null);tIsEntityRel2[2] |= (bean.getEn3Rel2Id() != null);
			tIsEntityRel1[3] |= (bean.getEn4Rel1Id() != null);tIsEntityRel2[3] |= (bean.getEn4Rel2Id() != null);
			tIsEntityRel1[4] |= (bean.getEn5Rel1Id() != null);tIsEntityRel2[4] |= (bean.getEn5Rel2Id() != null);
			tIsEntityRel1[5] |= (bean.getEn6Rel1Id() != null);tIsEntityRel2[5] |= (bean.getEn6Rel2Id() != null);
			tIsEntityRel1[6] |= (bean.getEn7Rel1Id() != null);tIsEntityRel2[6] |= (bean.getEn7Rel2Id() != null);
			tIsEntityRel1[7] |= (bean.getEn8Rel1Id() != null);tIsEntityRel2[7] |= (bean.getEn8Rel2Id() != null);
			tIsEntityRel1[8] |= (bean.getEn9Rel1Id() != null);tIsEntityRel2[8] |= (bean.getEn9Rel2Id() != null);
			tIsResult[0] |= (StringUtils.notEmpty(bean.getRsResult1()));
			tIsResult[1] |= (StringUtils.notEmpty(bean.getRsResult2()));
			tIsResult[2] |= (StringUtils.notEmpty(bean.getRsResult3()));
			tIsResult[3] |= (StringUtils.notEmpty(bean.getRsResult4()));
			tIsResult[4] |= (StringUtils.notEmpty(bean.getRsResult5()));
			isDates |= StringUtils.notEmpty(bean.getRsDate2());
			isPlace |= (bean.getCx1Id() != null || bean.getCx2Id() != null || bean.getCt2Id() != null || bean.getCt4Id() != null);
			isDraw |= (bean.getDrId() != null);
			isComment |= StringUtils.notEmpty(bean.getRsComment());
		}
		//entityCount /= (isDoubles ? 2 : 1);
		tColspan[0] += (tIsEntityRel1[0] ? 1 : 0) + (tIsEntityRel2[0] ? 1 : 0);
		tColspan[1] += (tIsEntityRel1[1] ? 1 : 0) + (tIsEntityRel2[1] ? 1 : 0);
		tColspan[2] += (tIsEntityRel1[2] ? 1 : 0) + (tIsEntityRel2[2] ? 1 : 0);
		tColspan[3] += (tIsEntityRel1[3] ? 1 : 0) + (tIsEntityRel2[3] ? 1 : 0);
		tColspan[4] += (tIsEntityRel1[4] ? 1 : 0) + (tIsEntityRel2[4] ? 1 : 0);
		tColspan[5] += (tIsEntityRel1[5] ? 1 : 0) + (tIsEntityRel2[5] ? 1 : 0);
		tColspan[6] += (tIsEntityRel1[6] ? 1 : 0) + (tIsEntityRel2[6] ? 1 : 0);
		tColspan[7] += (tIsEntityRel1[7] ? 1 : 0) + (tIsEntityRel2[7] ? 1 : 0);
		tColspan[8] += (tIsEntityRel1[8] ? 1 : 0) + (tIsEntityRel2[8] ? 1 : 0);
		boolean isScore = (entityCount > 1 && tIsResult[0] && !tIsResult[1] && !tIsResult[2] && !tIsResult[3] && !tIsResult[4]);
		tColspan[0] -= (isScore ? 1 : 0);

		long id = System.currentTimeMillis();
		ArrayList<String> lIds = new ArrayList<String>();
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		html.append("<thead><tr class='rsort'>" + (isComment ? "<th/>" : "") + "<th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("year", lang) + "</th>");
		for (int i = 1 ; i <= entityCount ; i++)
			html.append(i == 2 && isScore ? "<th onclick='sort(\"" + id + "\", this, " + i + ");'>" + ResourceUtils.getText("score", lang) + "</th>" : "").append("<th colspan='" + tColspan[i - 1] + "' onclick='sort(\"" + id + "\", this, " + i + ");'>" + (isMedal ? (i == 1 ? ImageUtils.getGoldHeader(lang) : (i == 2 ? ImageUtils.getSilverHeader(lang) : ImageUtils.getBronzeHeader(lang))) : ResourceUtils.getText("rank." + i, lang)) + "</th>");
		if (isDraw)
			html.append("<th/>");
		if (isDates)
			html.append("<th onclick='sort(\"" + id + "\", this, " + (entityCount + 1) + ");'>" + ResourceUtils.getText("date", lang) + "</th>");
		if (isPlace)
			html.append("<th onclick='sort(\"" + id + "\", this, " + (entityCount + (isDates ? 1 : 0) + 1) + ");'>" + ResourceUtils.getText("place", lang) + "</th>");
		html.append("</tr></thead><tbody id='tb-" + id + "'>");
		for (Object obj : coll) {
			ResultsBean bean = (ResultsBean) obj;
			lIds.add(String.valueOf(bean.getRsId()));

			// Evaluate bean
			String draw = "<table><tr><td><img alt='' src='img/render/draw.gif?3' title='" + ResourceUtils.getText("draw", lang) + "'/></td><td style='padding-left:3px;'><a href='ref?p=" + StringUtils.encode("DR-" + bean.getRsId()) + "'>" + ResourceUtils.getText("draw", lang) + "</a></td></tr></table>";
			String year = HtmlUtils.writeLink(Year.alias, bean.getYrId(), bean.getYrLabel(), null);
			String dates = (StringUtils.notEmpty(bean.getRsDate1()) ? StringUtils.toTextDate(bean.getRsDate1(), lang, "d MMM yyyy") + "&nbsp;&rarr;&nbsp;" : "") + (StringUtils.notEmpty(bean.getRsDate2()) ? StringUtils.toTextDate(bean.getRsDate2(), lang, "d MMM yyyy") : "");
			String place1 = null, place2 = null;
			String comment = (StringUtils.notEmpty(bean.getRsComment()) ? bean.getRsComment().replaceAll("\\|", "<br/>") : null);
			if (bean.getCx1Id() != null) {
				String img1 = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn1Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), bean.getCn1Label());
				String img2 = null;
				place1 = HtmlUtils.writeLink(Complex.alias, bean.getCx1Id(), bean.getCx1Label(), null);
				place1 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(City.alias, bean.getCt1Id(), bean.getCt1Label(), null);
				if (bean.getSt1Id() != null) {
					place1 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(State.alias, bean.getSt1Id(), bean.getSt1Code(), bean.getSt1Label());
					img2 = HtmlUtils.writeImage(ImageUtils.INDEX_STATE, bean.getSt1Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), bean.getSt1Label());
				}
				place1 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(Country.alias, bean.getCn1Id(), bean.getCn1Code(), bean.getCn1Label()); 
				if (img2 != null)
					place1 = HtmlUtils.writeImgTable(img2, place1);
				place1 = HtmlUtils.writeImgTable(img1, place1);
			}
			else if (bean.getCt2Id() != null) {
				String img1 = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn2Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), bean.getCn2Label());
				String img2 = null;
				place1 = HtmlUtils.writeLink(City.alias, bean.getCt2Id(), bean.getCt2Label(), null);
				if (bean.getSt2Id() != null) {
					place1 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(State.alias, bean.getSt2Id(), bean.getSt2Code(), bean.getSt2Label());
					img2 = HtmlUtils.writeImage(ImageUtils.INDEX_STATE, bean.getSt2Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), bean.getSt2Label());
				}
				place1 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(Country.alias, bean.getCn2Id(), bean.getCn2Code(), bean.getCn2Label());
				if (img2 != null)
					place1 = HtmlUtils.writeImgTable(img2, place1);
				place1 = HtmlUtils.writeImgTable(img1, place1);
			}
			if (bean.getCx2Id() != null) {
				String img1 = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn3Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), bean.getCn3Label());
				String img2 = null;
				place2 = HtmlUtils.writeLink(Complex.alias, bean.getCx2Id(), bean.getCx2Label(), null);
				place2 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(City.alias, bean.getCt3Id(), bean.getCt3Label(), null);
				if (bean.getSt3Id() != null) {
					place2 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(State.alias, bean.getSt3Id(), bean.getSt3Code(), bean.getSt3Label());
					img2 = HtmlUtils.writeImage(ImageUtils.INDEX_STATE, bean.getSt3Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), bean.getSt3Label());
				}
				place2 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(Country.alias, bean.getCn3Id(), bean.getCn3Code(), bean.getCn3Label()); 
				if (img2 != null)
					place2 = HtmlUtils.writeImgTable(img2, 	place2);
				place2 = HtmlUtils.writeImgTable(img1, 	place2);
			}
			else if (bean.getCt4Id() != null) {
				String img1 = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn4Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), bean.getCn4Label());
				String img2 = null;
				place2 = HtmlUtils.writeLink(City.alias, bean.getCt4Id(), bean.getCt4Label(), null);
				if (bean.getSt4Id() != null) {
					place2 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(State.alias, bean.getSt4Id(), bean.getSt4Code(), bean.getSt4Label());
					img2 = HtmlUtils.writeImage(ImageUtils.INDEX_STATE, bean.getSt4Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), bean.getSt4Label());
				}
				place2 += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(Country.alias, bean.getCn4Id(), bean.getCn4Code(), bean.getCn4Label());
				if (img2 != null)
					place2 = HtmlUtils.writeImgTable(img2, 	place2);
				place2 = HtmlUtils.writeImgTable(img1, 	place2);
			}
			ArrayList<Integer> listEqDoubles_ = new ArrayList<Integer>(listEqDoubles);
			if (isDoubles && StringUtils.notEmpty(bean.getRsExa()) && bean.getRsExa().equals("5-8"))
				listEqDoubles_.remove(listEqDoubles_.lastIndexOf(-1));
			List<Integer> listEq = (isDoubles ? listEqDoubles_ : StringUtils.tieList(bean.getRsExa()));
			String[] tEntity = {null, null, null, null, null, null, null, null, null};
			String[] tEntityRel = {null, null, null, null, null, null, null, null, null};
			String[] tEntityHtml = {null, null, null, null, null, null, null, null, null};
			String[] tResult = {null, null, null, null, null, null, null, null, null};
			if (bean.getRsRank1() != null) {
				tEntity[0] = getResultsEntity(type, bean.getRsRank1(), bean.getEn1Str1(), bean.getEn1Str2(), bean.getYrLabel());
				tEntityRel[0] = getResultsEntityRel(bean.getEn1Rel1Id(), bean.getEn1Rel1Code(), bean.getEn1Rel1Label(), bean.getEn1Rel2Id(), bean.getEn1Rel2Code(), bean.getEn1Rel2Label(), tIsEntityRel1[0], tIsEntityRel2[0], bean.getYrLabel());
				tResult[0] = bean.getRsResult1();
			}
			if (bean.getRsRank2() != null) {
				tEntity[1] = getResultsEntity(type, bean.getRsRank2(), bean.getEn2Str1(), bean.getEn2Str2(), bean.getYrLabel());
				tEntityRel[1] = getResultsEntityRel(bean.getEn2Rel1Id(), bean.getEn2Rel1Code(), bean.getEn2Rel1Label(), bean.getEn2Rel2Id(), bean.getEn2Rel2Code(), bean.getEn2Rel2Label(), tIsEntityRel1[1], tIsEntityRel2[1], bean.getYrLabel());
				tResult[1] = bean.getRsResult2();
			}
			if (bean.getRsRank3() != null) {
				tEntity[2] = getResultsEntity(type, bean.getRsRank3(), bean.getEn3Str1(), bean.getEn3Str2(), bean.getYrLabel());
				tEntityRel[2] = getResultsEntityRel(bean.getEn3Rel1Id(), bean.getEn3Rel1Code(), bean.getEn3Rel1Label(), bean.getEn3Rel2Id(), bean.getEn3Rel2Code(), bean.getEn3Rel2Label(), tIsEntityRel1[2], tIsEntityRel2[2], bean.getYrLabel());
				tResult[2] = bean.getRsResult3();
			}
			if (bean.getRsRank4() != null) {
				tEntity[3] = getResultsEntity(type, bean.getRsRank4(), bean.getEn4Str1(), bean.getEn4Str2(), bean.getYrLabel());
				tEntityRel[3] = getResultsEntityRel(bean.getEn3Rel1Id(), bean.getEn4Rel1Code(), bean.getEn4Rel1Label(), bean.getEn4Rel2Id(), bean.getEn4Rel2Code(), bean.getEn4Rel2Label(), tIsEntityRel1[3], tIsEntityRel2[3], bean.getYrLabel());
				tResult[3] = bean.getRsResult4();
			}
			if (bean.getRsRank5() != null) {
				tEntity[4] = getResultsEntity(type, bean.getRsRank5(), bean.getEn5Str1(), bean.getEn5Str2(), bean.getYrLabel());
				tEntityRel[4] = getResultsEntityRel(bean.getEn5Rel1Id(), bean.getEn5Rel1Code(), bean.getEn5Rel1Label(), bean.getEn5Rel2Id(), bean.getEn5Rel2Code(), bean.getEn5Rel2Label(), tIsEntityRel1[4], tIsEntityRel2[4], bean.getYrLabel());
				tResult[4] = bean.getRsResult5();
			}
			if (bean.getRsRank6() != null) {
				tEntity[5] = getResultsEntity(type, bean.getRsRank6(), bean.getEn6Str1(), bean.getEn6Str2(), bean.getYrLabel());
				tEntityRel[5] = getResultsEntityRel(bean.getEn6Rel1Id(), bean.getEn6Rel1Code(), bean.getEn6Rel1Label(), bean.getEn6Rel2Id(), bean.getEn6Rel2Code(), bean.getEn6Rel2Label(), tIsEntityRel1[5], tIsEntityRel2[5], bean.getYrLabel());
			}
			if (bean.getRsRank7() != null) {
				tEntity[6] = getResultsEntity(type, bean.getRsRank7(), bean.getEn7Str1(), bean.getEn7Str2(), bean.getYrLabel());
				tEntityRel[6] = getResultsEntityRel(bean.getEn7Rel1Id(), bean.getEn7Rel1Code(), bean.getEn7Rel1Label(), bean.getEn7Rel2Id(), bean.getEn7Rel2Code(), bean.getEn7Rel2Label(), tIsEntityRel1[6], tIsEntityRel2[6], bean.getYrLabel());
			}
			if (bean.getRsRank8() != null) {
				tEntity[7] = getResultsEntity(type, bean.getRsRank8(), bean.getEn8Str1(), bean.getEn8Str2(), bean.getYrLabel());
				tEntityRel[7] = getResultsEntityRel(bean.getEn8Rel1Id(), bean.getEn8Rel1Code(), bean.getEn8Rel1Label(), bean.getEn8Rel2Id(), bean.getEn8Rel2Code(), bean.getEn8Rel2Label(), tIsEntityRel1[7], tIsEntityRel2[7], bean.getYrLabel());
			}
			if (bean.getRsRank9() != null) {
				tEntity[8] = getResultsEntity(type, bean.getRsRank9(), bean.getEn9Str1(), bean.getEn9Str2(), bean.getYrLabel());
				tEntityRel[8] = getResultsEntityRel(bean.getEn9Rel1Id(), bean.getEn9Rel1Code(), bean.getEn9Rel1Label(), bean.getEn9Rel2Id(), bean.getEn9Rel2Code(), bean.getEn9Rel2Label(), tIsEntityRel1[8], tIsEntityRel2[8], bean.getYrLabel());
			}
			if (listEq != null && !listEq.isEmpty()) {
				Integer idx = listEq.get(0) - 1;
				for (int i = 1 ; i < listEq.size() ; i++) {
					if (idx == null)
						idx = listEq.get(i) - 1;
					else {
						if (listEq.get(i) == -1)
							idx = null;
						if (idx != null) {
							if (tEntity[idx] != null && tEntity[listEq.get(i) - 1] != null) {
								tEntity[idx] = tEntity[idx].concat((type < 10 ? "<br/>" : "") + tEntity[listEq.get(i) - 1].replaceAll("<table>", "<table class='margintop'>"));
								tEntity[listEq.get(i) - 1] = null;
							}
							if (tEntityRel[idx] != null && tEntityRel[listEq.get(i) - 1] != null) {
								tEntityRel[idx] = tEntityRel[idx].concat(tEntityRel[listEq.get(i) - 1]).replaceAll("\\<\\/td\\>\\<td\\>\\<table\\>", "<table class='margintop'>");
								tEntityRel[listEq.get(i) - 1] = null;
								if (tEntityRel[idx].matches(".*CN\\-\\d+.*") && tEntityRel[idx].matches(".*TM\\-\\d+.*")) {
									String[] t = tEntityRel[idx].split("</table>");
									StringBuffer sbCN = new StringBuffer();
									StringBuffer sbTM = new StringBuffer();
									for (String s : t) {
										if (s.matches(".*CN\\-\\d+.*"))
											sbCN.append(s.replaceAll("^\\<td\\>|\\<\\/td\\>$|\\<\\/td\\>\\<td\\>| class='margintop'", "").replaceAll("\\<table\\>", "<table class='marginbottom'>")).append("</table>");
										if (s.matches(".*TM\\-\\d+.*"))
											sbTM.append(s.replaceAll("^\\<td\\>|\\<\\/td\\>$|\\<\\/td\\>\\<td\\>| class='margintop'", "").replaceAll("\\<table\\>", "<table class='marginbottom'>")).append("</table>");
									}
									tEntityRel[idx] = "<td>" + sbCN.toString() + "</td><td>" + sbTM.toString() + "</td>";
								}
							}
						}
					}
				}
//				tEntity = StringUtils.removeNulls(tEntity);
//				tEntityRel = StringUtils.removeNulls(tEntityRel);
			}
			for (int i = 0 ; i < 9 ; i++)
				if (tEntity[i] != null)
					tEntityHtml[i] = ("<td class='srt'" + (i == 0 ? " style='font-weight:bold;'" : "") + ">" + tEntity[i] + "</td>" + (StringUtils.notEmpty(tEntityRel[i]) ?  tEntityRel[i] : (tIsEntityRel1[i] ? "<td></td>" : "") + (tIsEntityRel2[i] ? "<td></td>" : ""))) + (StringUtils.notEmpty(tResult[i]) ? "<td" + (isScore && i == 0 ? " class='centered'" : "") + ">" + tResult[i] + "</td>" : (tIsResult[i] ? "<td></td>" : ""));
				
			String commentTitle = null;
			String commentColor = null;
			if (comment != null && comment.matches("^\\#\\#(Clay|Decoturf|Grass|Gravel|Hard|Rebound|Snow|Tarmac).*")) {
				commentTitle = ResourceUtils.getText("info." + comment.substring(2).toLowerCase(), lang);
				commentColor = StringUtils.getCommentColor(commentTitle);
				comment = null;
			}
			
			// Write line
			html.append("<tr>");
			html.append(isComment ? "<td" + (StringUtils.notEmpty(commentTitle) ? " title='" + commentTitle + "' style='width:15px;background-color:" + commentColor + "';" : "") + ">" + (comment != null ? HtmlUtils.writeComment(bean.getRsId(), comment) : "") + "</td>" : "");
			html.append("<td class='srt'>" + year + "</td>");
			for (int i = 0 ; i < 9 ; i++)
				html.append(tEntityHtml[i] != null ? tEntityHtml[i] : (entityCount > i ? "<td class='srt' colspan=" + tColspan[i] + ">" + StringUtils.EMPTY + "</td>" + (isScore && i == 0 ? "<td class='srt'>" + StringUtils.EMPTY + "</td>" : "") : ""));
			html.append(isDraw ? "<td>" + (bean.getDrId() != null ? draw : "") + "</td>" : "");
			html.append(isDates ? "<td class='srt'>" + (StringUtils.notEmpty(dates) ? dates : "") + "</td>" : "");
			html.append((isPlace ? "<td class='srt'>" + (StringUtils.notEmpty(place1) ? place1 : "") + (StringUtils.notEmpty(place2) ? place2 : "") + "</td>" : "") + "</tr>");
		}
		html.append("</tbody></table>");
		html.append(getWinRecords(StringUtils.implode(lIds, ","), lang));
		return html;
	}

	public static void convertTreeArray(Collection<Object> coll, Writer writer, boolean encode) throws IOException {
		writer.write("treeItems=[" + (!encode ? "['',null," : ""));
		ArrayList<Object> lst = new ArrayList<Object>(coll);
		int i, j, k, l, m;
		for (i = 0 ; i < lst.size() ; i++) {
			TreeItem item = (TreeItem) lst.get(i);
			writer.write(i > 0 ? "," : "");
			writer.write("['" + StringUtils.toTree(item.getLabel()) + "','" + item.getIdItem() + "',");
			for (j = i + 1 ; j < lst.size() ; j++) {
				TreeItem item2 = (TreeItem) lst.get(j);
				if (item2.getLevel() < 2) {j--; break;}
				writer.write(j > i + 1 ? "," : "");
				writer.write("['" + StringUtils.toTree(item2.getLabel()) + "','" + (encode ? "enc-" + StringUtils.encode("RS-" + item.getIdItem() + "-" + item2.getIdItem()) : item.getIdItem() + "_" + item2.getIdItem()) + "',");
				for (k = j + 1 ; k < lst.size() ; k++) {
					TreeItem item3 = (TreeItem) lst.get(k);
					if (item3.getLevel() < 3) {k--; break;}
					writer.write(k > j + 1 ? "," : "");
					writer.write("['" + StringUtils.toTree(item3.getLabel()) + "','" + (encode ? "enc-" + StringUtils.encode("RS-" + item.getIdItem() + "-" + item2.getIdItem() + "-" + item3.getIdItem()) : item.getIdItem() + "_" + item2.getIdItem() + "_" + item3.getIdItem()) + "',");
					for (l = k + 1 ; l < lst.size() ; l++) {
						TreeItem item4 = (TreeItem) lst.get(l);
						if (item4.getLevel() < 4) {l--; break;}
						writer.write(l > k + 1 ? "," : "");
						writer.write("['" + StringUtils.toTree(item4.getLabel()) + "','" + (encode ? "enc-" + StringUtils.encode("RS-" + item.getIdItem() + "-" + item2.getIdItem() + "-" + item3.getIdItem() + "-" + item4.getIdItem()) : item.getIdItem() + "_" + item2.getIdItem() + "_" + item3.getIdItem() + "_" + item4.getIdItem()) + "',");
						for (m = l + 1 ; m < lst.size() ; m++) {
							TreeItem item5 = (TreeItem) lst.get(m);
							if (item5.getLevel() < 5) {m--; break;}
							writer.write(m > l + 1 ? "," : "");
							writer.write("['" + StringUtils.toTree(item5.getLabel()) + "','" + (encode ? "enc-" + StringUtils.encode("RS-" + item.getIdItem() + "-" + item2.getIdItem() + "-" + item3.getIdItem() + "-" + item4.getIdItem() + "-" + item5.getIdItem()) : item.getIdItem() + "_" + item2.getIdItem() + "_" + item3.getIdItem() + "_" + item4.getIdItem() + "_" + item5.getIdItem()) + "']");
						}
						l = m;
						writer.write("]");
					}
					k = l;
					writer.write("]");
				}
				j = k;
				writer.write("]");
			}
			i = j;
			writer.write("]");
		}
		writer.write((!encode ? "]" : "") + "];");
	}

	public static StringBuffer convertSearch(Collection<Object> coll, String pattern, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		String currentEntity = "";
		HashMap<String, Integer> hCount = new HashMap<String, Integer>();
		// Evaluate items
		for (Object obj : coll) {
			String s = ((RefItem) obj).getEntity();
			s = (s.equals(Championship.alias) ? Event.alias : s);
			if (!hCount.containsKey(s))
				hCount.put(s, 0);
			hCount.put(s, hCount.get(s) + 1);
		}
		// Write items
		String img = null;
		String link = null;
		for (Object obj : coll) {
			RefItem item = (RefItem) obj;
			String en = item.getEntity();
			en = (en.equals(Championship.alias) ? Event.alias : en);
			if (!en.equals(currentEntity)) {
				String id = en + "-" + System.currentTimeMillis();
				int sortIndex = 0;
				html.append(StringUtils.notEmpty(currentEntity) ? "</tbody></table><table class='tsort'>" : "");
				StringBuffer cols = new StringBuffer("<th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>" + ResourceUtils.getText("name", lang) + "</th>");
//				if (en.matches("PR|TM"))
//					cols.append("<th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>Country</th><th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>Sport</th>" + (en.matches("PR") ? "<th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>Team</th>" : ""));
				if (en.matches("PR"))
					cols.append("<th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>" + ResourceUtils.getText("country", lang) + "</th><th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>" + ResourceUtils.getText("sport", lang) + "</th>");				
				else if (en.matches("TM"))
					cols.append("<th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>" + ResourceUtils.getText("sport", lang) + "</th>");
				else if (en.matches("CX|CT"))
					cols.append((en.matches("CX") ? "<th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>" + ResourceUtils.getText("city", lang) + "</th>" : "") + "<th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>" + ResourceUtils.getText("country", lang) + "</th>");
				html.append("<thead><tr><th colspan=" + (StringUtils.countIn(cols.toString(), "<th") + 1) + ">");
				html.append(HtmlUtils.writeToggleTitle(ResourceUtils.getText("entity." + en, lang) + "&nbsp;(" + hCount.get(en) + ")") + "</th></tr>");
				html.append("<tr class='rsort'>" + cols.toString() + "<th onclick='sort(\"" + id + "\", this, " + sortIndex++ + ");'>" + ResourceUtils.getText("references", lang) + "</th></tr></thead><tbody id='tb-" + id + "'>");
				currentEntity = en;
			}
			String name = item.getLabel();
			int p1 = name.toLowerCase().indexOf(pattern.toLowerCase());
			if (p1 > -1) {
				int p2 = p1 + pattern.length();
				name = name.substring(0, p1) + "<b>" + name.substring(p1, p2) + "</b>" + name.substring(p2);
			}
			name = HtmlUtils.writeLink(item.getEntity(), item.getIdItem(), name, null);
			if (item.getEntity().matches("CP|EV|CN|OL|SP|ST|TM")) {
				img = HtmlUtils.writeImage(ImageUtils.getIndex(item.getEntity()), item.getIdItem(), ImageUtils.SIZE_SMALL, null, null);
				name = HtmlUtils.writeImgTable(img, name);
			}
			// Handle relations
			String rel1 = "";
			String rel2 = "";
			String rel3 = "";
			if (en.matches("PR|TM")) {
				if(en.equals("PR") && item.getLink() != null && item.getLink() == 0) {
					ArrayList<Integer> lRel1Id = new ArrayList<Integer>();
					ArrayList<Integer> lRel2Id = new ArrayList<Integer>();
					ArrayList<Integer> lRel3Id = new ArrayList<Integer>();
					StringBuffer sbRel1 = new StringBuffer();
					StringBuffer sbRel2 = new StringBuffer();
					StringBuffer sbRel3 = new StringBuffer();
					for (Athlete a : (List<Athlete>) DatabaseHelper.execute("from Athlete where id = " + item.getIdItem() + " or link = " + item.getIdItem() + " order by id")) {
						if (a.getCountry() != null && !lRel1Id.contains(a.getCountry().getId())) {
							link = HtmlUtils.writeLink(Country.alias, a.getCountry().getId(), a.getCountry().getLabel(lang) + " (" + a.getCountry().getCode() + ")", null);
							img = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, a.getCountry().getId(), ImageUtils.SIZE_SMALL, null, null);
							sbRel1.append(HtmlUtils.writeImgTable(img, link));
							lRel1Id.add(a.getCountry().getId());
						}
						if (a.getSport() != null && !lRel2Id.contains(a.getSport().getId())) {
							link = HtmlUtils.writeLink(Sport.alias, a.getSport().getId(), a.getSport().getLabel(lang), null);
							img = HtmlUtils.writeImage(ImageUtils.INDEX_SPORT, a.getSport().getId(), ImageUtils.SIZE_SMALL, null, null);
							sbRel2.append(HtmlUtils.writeImgTable(img, link));
							lRel2Id.add(a.getSport().getId());
						}
						if (a.getTeam() != null && !lRel3Id.contains(a.getTeam().getId())) {
							link = HtmlUtils.writeLink(Team.alias, a.getTeam().getId(), a.getTeam().getLabel(), null);
							img = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, a.getTeam().getId(), ImageUtils.SIZE_SMALL, null, null);
							sbRel3.append(HtmlUtils.writeImgTable(img, link));
							lRel3Id.add(a.getTeam().getId());
						}
					}
					rel1 = sbRel1.toString().replaceAll("</table><table>", "</table><table class='margintop'>");
					rel2 = sbRel2.toString().replaceAll("</table><table>", "</table><table class='margintop'>");
					rel3 = sbRel3.toString().replaceAll("</table><table>", "</table><table class='margintop'>");
				}
				else {
					if (item.getIdRel1() != null && !en.matches("TM")) {
						link = HtmlUtils.writeLink(Country.alias, item.getIdRel1(), item.getLabelRel1(), null);
						img = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel1(), ImageUtils.SIZE_SMALL, null, null);
						rel1 = HtmlUtils.writeImgTable(img, link);
					}
					if (item.getIdRel2() != null) {
						link = HtmlUtils.writeLink(Sport.alias, item.getIdRel2(), item.getLabelRel2(), null);
						img = HtmlUtils.writeImage(ImageUtils.INDEX_SPORT, item.getIdRel2(), ImageUtils.SIZE_SMALL, null, null);
						rel2 = HtmlUtils.writeImgTable(img, link);
					}
					if (item.getIdRel3() != null && !en.matches("PR|TM")) {
						link = HtmlUtils.writeLink(Team.alias, item.getIdRel3(), item.getLabelRel3(), null);
						img = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, item.getIdRel3(), ImageUtils.SIZE_SMALL, null, null);
						rel3 = HtmlUtils.writeImgTable(img, link);
					}
				}
			}
			else if (en.matches("CX|CT")) {
				if (item.getIdRel1() != null && en.matches("CX")) {
					link = HtmlUtils.writeLink(City.alias, item.getIdRel1(), item.getLabelRel1(), null);
					rel1 = link;
				}
//				if (item.getIdRel2() != null) {
//					link = HtmlUtils.writeLink(State.alias, item.getIdRel2(), item.getLabelRel2(), null);
//					img = HtmlUtils.writeImage(ImageUtils.INDEX_STATE, item.getIdRel2(), ImageUtils.SIZE_SMALL, null, null);
//					rel2 = HtmlUtils.writeImgTable(img, link);
//				}
				if (item.getIdRel3() != null) {
					link = HtmlUtils.writeLink(Country.alias, item.getIdRel3(), item.getLabelRel3(), null);
					img = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, item.getIdRel3(), ImageUtils.SIZE_SMALL, null, null);
					rel3 = HtmlUtils.writeImgTable(img, link);
				}
			}
			if (en.matches("PR|TM|CX|CT")) {
				rel1 = (!en.matches("CT|TM") ? "<td class='srt'>" + (StringUtils.notEmpty(rel1) ? rel1 : StringUtils.EMPTY) + "</td>" : "");
				rel2 = (!en.matches("CX|CT") ? "<td class='srt'>" + (StringUtils.notEmpty(rel2) ? rel2 : StringUtils.EMPTY) + "</td>" : "");
				rel3 = (!en.matches("PR|TM") ? "<td class='srt'>" + (StringUtils.notEmpty(rel3) ? rel3 : StringUtils.EMPTY) + "</td>" : "");
			}

			// Write line
			html.append("<tr><td class='srt'>" + name + "</td>" + rel1 + rel2 + rel3);
			html.append("<td class='centered srt'>" + (item.getCountRef() != null ? item.getCountRef() : 0) + "</td>");
			html.append("</tr>");
		}
		html.append("</tbody></table>");
		return html;
	}

	public static StringBuffer convertOlympicMedals(Collection<Object> coll, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		int currentOlympics = 0;
		boolean isIndividual = false;
		boolean isResult = false;
		boolean isScore = false;
		// Evaluate columns
		for (Object obj : coll) {
			OlympicMedalsBean bean = (OlympicMedalsBean) obj;
			isIndividual |= ((bean.getTp2Number() != null ? bean.getTp2Number() : bean.getTp1Number()) <= 10);
			isScore |= (bean.getRsRank1() != null && bean.getRsRank2() != null && StringUtils.notEmpty(bean.getRsResult1()) && !StringUtils.notEmpty(bean.getRsResult2()) && !StringUtils.notEmpty(bean.getRsResult3()));
			isResult |= (StringUtils.notEmpty(bean.getRsResult1()) && !isScore);
		}
		int colspan = (isIndividual ? 2 : 1) + (isResult ? 1 : 0);
		String olympics = null;
		String tmpImg = null;
		long id = System.currentTimeMillis();
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		html.append("<thead><tr class='rsort'><th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("entity.OL", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("event", lang) + "</th>");
		html.append("<th colspan='" + colspan + "' onclick='sort(\"" + id + "\", this, 2);'>" + ImageUtils.getGoldHeader(lang) + "</th>");
		html.append(isScore ? "<th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("score", lang) + "</th>" : "");
		html.append("<th colspan='" + colspan + "' onclick='sort(\"" + id + "\", this, " + (3 + (isScore ? 1 : 0)) + ");'>" + ImageUtils.getSilverHeader(lang) + "</th>");
		html.append("<th colspan='" + colspan + "' onclick='sort(\"" + id + "\", this, " + (4 + (isScore ? 1 : 0)) + ");'>" + ImageUtils.getBronzeHeader(lang) + "</th>");
		html.append("<th colspan='" + colspan + "' onclick='sort(\"" + id + "\", this, " + (5 + (isScore ? 1 : 0)) + ");'>" + ResourceUtils.getText("place", lang) + "</th>");
		html.append("</tr></thead><tbody id='tb-" + id + "'>");
		for (Object obj : coll) {
			OlympicMedalsBean bean = (OlympicMedalsBean) obj;
			boolean isIndividual_ = ((bean.getTp2Number() != null ? bean.getTp2Number() : bean.getTp1Number()) <= 10);
			if (!bean.getYrId().equals(currentOlympics)) {
				tmpImg = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn1Id() != null ? bean.getCn1Id() : bean.getCn2Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null);
				olympics = HtmlUtils.writeLink(Olympics.alias, bean.getOlId(), bean.getYrLabel() + "&nbsp;" + bean.getOlCity(), null);
				olympics = HtmlUtils.writeImgTable(tmpImg, olympics);
				currentOlympics = bean.getYrId();
			}

			// Evaluate bean
			String entity1 = null, entityCn1 = null;
			String entity2 = null, entityCn2 = null;
			String entity3 = null, entityCn3 = null;
			String venue = null;
			if (bean.getRsRank1() != null) {
				if (isIndividual_) {
					entity1 = HtmlUtils.writeLink(Athlete.alias, bean.getRsRank1(), StringUtils.toFullName(bean.getPr1LastName(), bean.getPr1FirstName()), null);
					entityCn1 = getResultsEntityRel(null, null, null, bean.getPr1CnId(), bean.getPr1CnCode(), bean.getPr1CnLabel(), false, false, bean.getYrLabel());
				}
				else
					entity1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getRsRank1(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null), HtmlUtils.writeLink(Country.alias, bean.getRsRank1(), bean.getCn1Code(), null));
			}
			if (bean.getRsRank2() != null) {
				if (isIndividual_) {
					entity2 = HtmlUtils.writeLink(Athlete.alias, bean.getRsRank2(), StringUtils.toFullName(bean.getPr2LastName(), bean.getPr2FirstName()), null);
					entityCn2 = getResultsEntityRel(null, null, null, bean.getPr2CnId(), bean.getPr2CnCode(), bean.getPr2CnLabel(), false, false, bean.getYrLabel());
				}
				else
					entity2 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getRsRank2(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null), HtmlUtils.writeLink(Country.alias, bean.getRsRank2(), bean.getCn2Code(), null));
			}
			if (bean.getRsRank3() != null) {
				if (isIndividual_) {
					entity3 = HtmlUtils.writeLink(Athlete.alias, bean.getRsRank3(), StringUtils.toFullName(bean.getPr3LastName(), bean.getPr3FirstName()), null);
					entityCn3 = getResultsEntityRel(null, null, null, bean.getPr3CnId(), bean.getPr3CnCode(), bean.getPr3CnLabel(), false, false ,bean.getYrLabel());
				}
				else
					entity3 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getRsRank3(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null), HtmlUtils.writeLink(Country.alias, bean.getRsRank3(), bean.getCn3Code(), null));
			}
			if (bean.getCxId() != null) {
				tmpImg = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn1Id(), ImageUtils.SIZE_SMALL, null, null);				
				venue = HtmlUtils.writeLink(Complex.alias, bean.getCxId(), bean.getCxLabel(), null);
				venue += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(City.alias, bean.getCt1Id(), bean.getCt1Label(), null);
				if (bean.getSt1Id() != null)
					venue += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(State.alias, bean.getSt1Id(), bean.getSt1Code(), bean.getSt1Label());
				venue += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(Country.alias, bean.getCn1Id(), bean.getCn1Code_(), bean.getCn1Label_()); 
				venue = HtmlUtils.writeImgTable(tmpImg, venue);
			}

			// Write line
			html.append("<tr><td class='srt'>" + olympics + "</td><td class='srt'>" + bean.getEvLabel() + (StringUtils.notEmpty(bean.getSeLabel()) ? " - " + bean.getSeLabel() : "") + "</td>");
			html.append(entity1 != null ? "<td class='srt'" + (!isIndividual_ && isIndividual ? " colspan='2'" : "") + " style='font-weight:bold;'>" + entity1 + "</td>" + (entityCn1 != null ? entityCn1 : "") + (isResult ? "<td>" + (bean.getRsResult1() != null ? bean.getRsResult1() : "") + "</td>" : "") : "<td colspan=" + colspan + ">" + StringUtils.EMPTY + "</td>");
			html.append(isScore ? "<td class='srt'>" + bean.getRsResult1() + "</td>" : "");
			html.append(entity2 != null ? "<td class='srt'" + (!isIndividual_ && isIndividual ? " colspan='2'" : "") + ">" + entity2 + "</td>" + (entityCn2 != null ? entityCn2 : "") + (isResult ? "<td>" + (bean.getRsResult2() != null ? bean.getRsResult2() : "") + "</td>" : "") : "<td colspan=" + colspan + ">" + StringUtils.EMPTY + "</td>");
			html.append(entity3 != null ? "<td class='srt'" + (!isIndividual_ && isIndividual ? " colspan='2'" : "") + ">" + entity3 + "</td>" + (entityCn3 != null ? entityCn3 : "") + (isResult ? "<td>" + (bean.getRsResult3() != null ? bean.getRsResult3() : "") + "</td>" : "") : "<td colspan=" + colspan + ">" + StringUtils.EMPTY + "</td>");
			html.append("<td class='srt'>" + (StringUtils.notEmpty(venue) ? venue : "-") + "</td>");
			html.append("</tr>");
		}
		html.append("</tbody></table>");
		return html;
	}

	public static StringBuffer convertOlympicRankings(Collection<Object> coll, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		int currentOlympics = 0;
		String olympics = null;
		String tmpImg = null;
		long id = System.currentTimeMillis();
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		html.append("<thead><tr class='rsort'><th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("year", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("country", lang) + "</th>");
		html.append("<th onclick='sort(\"" + id + "\", this, 2);'>" + ImageUtils.getGoldHeader(lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ImageUtils.getSilverHeader(lang) + "</th><th onclick='sort(\"" + id + "\", this, 4);'>" + ImageUtils.getBronzeHeader(lang) + "</th>");
		html.append("</tr></thead><tbody id='tb-" + id + "'>");
		for (Object obj : coll) {
			OlympicRankingsBean bean = (OlympicRankingsBean) obj;
			if (!bean.getYrId().equals(currentOlympics)) {
				tmpImg = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn2Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null);
				olympics = HtmlUtils.writeLink(Olympics.alias, bean.getOlId(), bean.getYrLabel() + "&nbsp;" + bean.getCtLabel(), null);
				olympics = HtmlUtils.writeImgTable(tmpImg, olympics);
				currentOlympics = bean.getYrId();
			}

			// Evaluate bean
			String country = HtmlUtils.writeLink(Country.alias, bean.getCn1Id(), bean.getCn1Label(), null);
			tmpImg = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn1Id(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null);
			country = HtmlUtils.writeImgTable(tmpImg, country);

			// Write line
			html.append("<tr><td class='srt'>" + olympics + "</td><td class='srt'>" + country + "</td>");
			html.append("<td class='srt'>" + bean.getOrCountGold() + "</td>");
			html.append("<td class='srt'>" + bean.getOrCountSilver() + "</td>");
			html.append("<td class='srt'>" + bean.getOrCountBronze() + "</td></tr>");
		}
		html.append("</tbody></table>");
		return html;
	}

	public static StringBuffer convertHallOfFame(Collection<Object> coll, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		long id = System.currentTimeMillis();
		html.append("<thead><tr class='rsort'><th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("year", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("inductee", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("inducted.as", lang) + "</th></tr></thead><tbody id='tb-" + id + "'>");
		for (Object obj : coll) {
			HallOfFameBean bean = (HallOfFameBean) obj;

			// Evaluate bean
			String year = HtmlUtils.writeLink(Year.alias, bean.getYrId(), bean.getYrLabel(), null);
			String name = HtmlUtils.writeLink(Athlete.alias, bean.getPrId(), StringUtils.toFullName(bean.getPrLastName(), bean.getPrFirstName()), null);
			String position = "-";
			if (StringUtils.notEmpty(bean.getHfPosition())) {
				StringBuffer sbPos = new StringBuffer();
				for (String s : bean.getHfPosition().split("\\-"))
					sbPos.append((!sbPos.toString().isEmpty() ? "&nbsp;/&nbsp;" : "") + StringUtils.getUSPosition(bean.getLgId(), s));
				position = sbPos.toString();
			}
				
			// Write line
			html.append("<tr><td class='srt'>" + year + "</td><td class='srt'><b>" + name + "</b></td><td class='srt'>" + position + "</td></tr>");
		}
		html.append("</tbody></table>");
		return html;
	}

	public static StringBuffer convertRetiredNumber(Collection<Object> coll, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		long id = System.currentTimeMillis();
		html.append("<thead><tr class='rsort'><th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("team", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("number", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("name", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("year", lang) + "</th></tr></thead><tbody id='tb-" + id + "'>");
		for (Object obj : coll) {
			RetiredNumberBean bean = (RetiredNumberBean) obj;

			// Evaluate bean
			String teamImg = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getTmId(), ImageUtils.SIZE_SMALL, null, null);
			String team = HtmlUtils.writeLink(Team.alias, bean.getTmId(), bean.getTmLabel(), null);
			team = HtmlUtils.writeImgTable(teamImg, team);
			String name = HtmlUtils.writeLink(Athlete.alias, bean.getPrId(), StringUtils.toFullName(bean.getPrLastName(), bean.getPrFirstName()), null);
			String number = String.valueOf(bean.getRnNumber());
			String year = (bean.getYrId() != null ? HtmlUtils.writeLink(Year.alias, bean.getYrId(), bean.getYrLabel(), null) : "-");
			
			// Write line
			html.append("<tr><td class='srt'>" + team + "</td><td class='srt' width='50'>" + number + "</td><td class='srt'><b>" + name + "</b></td><td class='srt'>" + year + "</td></tr>");
		}
		html.append("</tbody></table>");
		return html;
	}

	public static StringBuffer convertTeamStadium(Collection<Object> coll, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		long id = System.currentTimeMillis();
		html.append("<thead><tr class='rsort'><th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("team", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("complex", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("city", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("years", lang) + "</th></tr></thead><tbody id='tb-" + id + "'>");
		for (Object obj : coll) {
			TeamStadiumBean bean = (TeamStadiumBean) obj;

			// Evaluate bean
			String teamImg = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getTmId(), ImageUtils.SIZE_SMALL, bean.getTsDate1(), null);
			String team = HtmlUtils.writeLink(Team.alias, bean.getTmId(), bean.getTmLabel(), null);
			team = HtmlUtils.writeImgTable(teamImg, team);
			String tmpImg = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCnId(), ImageUtils.SIZE_SMALL, bean.getTsDate1(), null);
			String complex = HtmlUtils.writeLink(Complex.alias, bean.getCxId(), bean.getCxLabel(), null);
			String city = HtmlUtils.SPACE + HtmlUtils.writeLink(City.alias, bean.getCtId(), bean.getCtLabel(), null);
			if (bean.getStId() != null)
				city += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(State.alias, bean.getStId(), bean.getStCode(), bean.getStLabel());
			city += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(Country.alias, bean.getCnId(), bean.getCnCode(), bean.getStLabel()); 
			city = HtmlUtils.writeImgTable(tmpImg, city);	
			String years = bean.getTsDate1() + "&nbsp;&rarr;&nbsp;" + (!bean.getTsDate2().equals("0") ? bean.getTsDate2() : ResourceUtils.getText("today", lang));

			// Write line
			html.append("<tr><td class='srt'>" + team + "</td><td class='srt'>" + complex + (bean.getTsRenamed() ? "<b>*</b>" : "") + "</td><td class='srt'>" + city + "</td><td class='srt'>" + years + "</td></tr>");
		}
		html.append("</tbody></table>");
		return html;
	}

	public static StringBuffer convertUSChampionships(Collection<Object> coll, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		boolean isDate = false;
		boolean isPlace = false;
		boolean isComment = false;
		for (Object obj : coll) {
			USChampionshipsBean bean = (USChampionshipsBean) obj;
			isDate |= StringUtils.notEmpty(bean.getRsDate2());
			isPlace |= (bean.getCxId() != null);
			isComment |= StringUtils.notEmpty(bean.getRsComment()); 
		}
		long id = System.currentTimeMillis();
		html.append("<thead><tr class='rsort'><th" + (isComment ? " colspan='2'" : "") + " onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("year", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("champion", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("score", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("runner.up", lang) + "</th>");
		html.append((isDate ? "<th onclick='sort(\"" + id + "\", this, 4);'>" + ResourceUtils.getText("date", lang) + "</th>" : "") + (isPlace ? "<th onclick='sort(\"" + id + "\", this, " + (isDate ? 5 : 4) + ");'>" + ResourceUtils.getText("place", lang) + "</th>" : "") + "</tr></thead><tbody id='tb-" + id + "'>");
		for (Object obj : coll) {
			USChampionshipsBean bean = (USChampionshipsBean) obj;

			// Evaluate bean
			String champion = null;
			String runnerup = null;
			if (bean.getRsRank1() != null) {
				String championImg = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRsRank1(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null);
				champion = HtmlUtils.writeLink(Team.alias, bean.getRsRank1(), bean.getRsTeam1(), null);
				champion = HtmlUtils.writeImgTable(championImg, champion);	
			}
			if (bean.getRsRank2() != null) {
				String runnerupImg = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRsRank2(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null);
				runnerup = HtmlUtils.writeLink(Team.alias, bean.getRsRank2(), bean.getRsTeam2(), null);
				runnerup = HtmlUtils.writeImgTable(runnerupImg, runnerup);
			}
			String year = HtmlUtils.writeLink(Year.alias, bean.getYrId(), bean.getYrLabel(), null);
			String date = (StringUtils.notEmpty(bean.getRsDate1()) ? StringUtils.toTextDate(bean.getRsDate1(), lang, null) + "&nbsp;&rarr;&nbsp;" : "") + (StringUtils.notEmpty(bean.getRsDate2()) ? StringUtils.toTextDate(bean.getRsDate2(), lang, null) : StringUtils.EMPTY);
			String place = StringUtils.EMPTY;
			if (bean.getCxId() != null) {
				String tmpImg = HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCnId(), ImageUtils.SIZE_SMALL, bean.getYrLabel(), null);
				place = HtmlUtils.writeLink(Complex.alias, bean.getCxId(), bean.getCxLabel(), null);
				place += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(City.alias, bean.getCtId(), bean.getCtLabel(), null);
				if (bean.getStId() != null)
					place += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(State.alias, bean.getStId(), bean.getStCode(), bean.getStLabel());
				place += "," + HtmlUtils.SPACE + HtmlUtils.writeLink(Country.alias, bean.getCnId(), bean.getCnCode(), bean.getCnLabel()); 
				place = HtmlUtils.writeImgTable(tmpImg, place);
			}

			// Write line
			html.append("<tr>" + (isComment ? "<td>" + HtmlUtils.writeComment(bean.getRsId(), bean.getRsComment()) + "</td>" : "") + "<td class='srt'>" + year + "</td><td class='srt'>" + (champion != null ? champion : StringUtils.EMPTY) + "</td><td class='srt'>" + (StringUtils.notEmpty(bean.getRsResult()) ? bean.getRsResult() : "") + "</td>");
			html.append("<td class='srt'>" + (runnerup != null ? runnerup : StringUtils.EMPTY) + "</td>" + (isDate ? "<td class='srt'>" + date + "</td>" : "") + (isPlace ? "<td class='srt'>" + place + "</td>" : "") + "</tr>");
		}
		html.append("</tbody></table>");
		return html;
	}

	public static StringBuffer convertUSRecords(Collection<Object> coll, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		String currentCategory = "";
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		long id = 0;
		for (Object obj : coll) {
			USRecordsBean bean = (USRecordsBean) obj;
			if (!currentCategory.equals(bean.getSeLabel())) {
				id = System.currentTimeMillis();
				html.append(StringUtils.notEmpty(currentCategory) ? "</tbody></table><table class='tsort'>" : "");
				html.append("<thead><tr><th colspan='7'>" + HtmlUtils.writeToggleTitle(bean.getSeLabel().toUpperCase()) + "</th></tr>");
				html.append("<tr class='rsort'><th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("scope", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("type", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("period", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("record", lang) + "</th><th colspan='3' onclick='sort(\"" + id + "\", this, 4);'>" + ResourceUtils.getText("record.holder", lang) + "</th>");
				html.append("</tr></thead><tbody id='tb-" + id + "'>");
				currentCategory = bean.getSeLabel();
			}

			// Evaluate bean
			boolean isPerson = (bean.getRcNumber2() < 50);
			String[] tRank = new String[5];
			String[] tRecord = new String[5];
			String[] tDate = new String[5];
			tRank[0] = (isPerson && bean.getRcPerson1() != null ? HtmlUtils.writeLink(Athlete.alias, bean.getRcRank1(), StringUtils.toFullName(bean.getRcPerson1LastName(), bean.getRcPerson1FirstName()), null) : (bean.getRcTeam1() != null ? HtmlUtils.writeLink(Team.alias, bean.getRcRank1(), bean.getRcTeam1(), null) : null));
			tRank[1] = (isPerson && bean.getRcPerson2() != null ? HtmlUtils.writeLink(Athlete.alias, bean.getRcRank2(), StringUtils.toFullName(bean.getRcPerson2LastName(), bean.getRcPerson2FirstName()), null) : (bean.getRcTeam2() != null ? HtmlUtils.writeLink(Team.alias, bean.getRcRank2(), bean.getRcTeam2(), null) : null));
			tRank[2] = (isPerson && bean.getRcPerson3() != null ? HtmlUtils.writeLink(Athlete.alias, bean.getRcRank3(), StringUtils.toFullName(bean.getRcPerson3LastName(), bean.getRcPerson3FirstName()), null) : (bean.getRcTeam3() != null ? HtmlUtils.writeLink(Team.alias, bean.getRcRank3(), bean.getRcTeam3(), null) : null));
			tRank[3] = (isPerson && bean.getRcPerson4() != null ? HtmlUtils.writeLink(Athlete.alias, bean.getRcRank4(), StringUtils.toFullName(bean.getRcPerson4LastName(), bean.getRcPerson4FirstName()), null) : (bean.getRcTeam4() != null ? HtmlUtils.writeLink(Team.alias, bean.getRcRank4(), bean.getRcTeam4(), null) : null));
			tRank[4] = (isPerson && bean.getRcPerson5() != null ? HtmlUtils.writeLink(Athlete.alias, bean.getRcRank5(), StringUtils.toFullName(bean.getRcPerson5LastName(), bean.getRcPerson5FirstName()), null) : (bean.getRcTeam5() != null ? HtmlUtils.writeLink(Team.alias, bean.getRcRank5(), bean.getRcTeam5(), null) : null));
			tRank[0] = (tRank[0] != null && isPerson && bean.getRcIdPrTeam1() != null && bean.getRcType2().matches("Season|Game|Half|Quarter|Series|Period") ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcIdPrTeam1(), ImageUtils.SIZE_SMALL, null, bean.getRcPrTeam1()), tRank[0]) : (tRank[0] != null && !isPerson ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcRank1(), ImageUtils.SIZE_SMALL, bean.getRcDate1(), null), tRank[0]) : tRank[0]));
			tRank[1] = (tRank[1] != null && isPerson && bean.getRcIdPrTeam2() != null && bean.getRcType2().matches("Season|Game|Half|Quarter|Series|Period") ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcIdPrTeam2(), ImageUtils.SIZE_SMALL, null, bean.getRcPrTeam2()), tRank[1]) : (tRank[1] != null && !isPerson ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcRank2(), ImageUtils.SIZE_SMALL, bean.getRcDate1(), null), tRank[1]) : tRank[1]));
			tRank[2] = (tRank[2] != null && isPerson && bean.getRcIdPrTeam3() != null && bean.getRcType2().matches("Season|Game|Half|Quarter|Series|Period") ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcIdPrTeam3(), ImageUtils.SIZE_SMALL, null, bean.getRcPrTeam3()), tRank[2]) : (tRank[2] != null && !isPerson ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcRank3(), ImageUtils.SIZE_SMALL, bean.getRcDate1(), null), tRank[2]) : tRank[2]));
			tRank[3] = (tRank[3] != null && isPerson && bean.getRcIdPrTeam4() != null && bean.getRcType2().matches("Season|Game|Half|Quarter|Series|Period") ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcIdPrTeam4(), ImageUtils.SIZE_SMALL, null, bean.getRcPrTeam4()), tRank[3]) : (tRank[3] != null && !isPerson ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcRank4(), ImageUtils.SIZE_SMALL, bean.getRcDate1(), null), tRank[3]) : tRank[3]));
			tRank[4] = (tRank[4] != null && isPerson && bean.getRcIdPrTeam5() != null && bean.getRcType2().matches("Season|Game|Half|Quarter|Series|Period") ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcIdPrTeam5(), ImageUtils.SIZE_SMALL, null, bean.getRcPrTeam5()), tRank[4]) : (tRank[4] != null && !isPerson ? HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getRcRank5(), ImageUtils.SIZE_SMALL, bean.getRcDate1(), null), tRank[4]) : tRank[4]));
			tRecord[0] = (bean.getRcRecord1() != null ? bean.getRcRecord1() : null);
			tRecord[1] = (bean.getRcRecord2() != null ? bean.getRcRecord2() : null);
			tRecord[2] = (bean.getRcRecord3() != null ? bean.getRcRecord3() : null);
			tRecord[3] = (bean.getRcRecord4() != null ? bean.getRcRecord4() : null);
			tRecord[4] = (bean.getRcRecord5() != null ? bean.getRcRecord5() : null);
			tDate[0] = (bean.getEvLabel().equals("Super Bowl") ? bean.getEvLabel() + "&nbsp;" : "") + (bean.getRcDate1() != null ? bean.getRcDate1() : StringUtils.EMPTY);
			tDate[1] = (bean.getEvLabel().equals("Super Bowl") ? bean.getEvLabel() + "&nbsp;" : "") + (bean.getRcDate2() != null ? bean.getRcDate2() : StringUtils.EMPTY);
			tDate[2] = (bean.getEvLabel().equals("Super Bowl") ? bean.getEvLabel() + "&nbsp;" : "") + (bean.getRcDate3() != null ? bean.getRcDate3() : StringUtils.EMPTY);
			tDate[3] = (bean.getEvLabel().equals("Super Bowl") ? bean.getEvLabel() + "&nbsp;" : "") + (bean.getRcDate4() != null ? bean.getRcDate4() : StringUtils.EMPTY);
			tDate[4] = (bean.getEvLabel().equals("Super Bowl") ? bean.getEvLabel() + "&nbsp;" : "") + (bean.getRcDate5() != null ? bean.getRcDate5() : StringUtils.EMPTY);
			tDate[0] = (tDate[0] != null && tDate[0].matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d") ? StringUtils.toTextDate(tDate[0], lang, "dd MMM yyyy") : tDate[0]);
			tDate[1] = (tDate[1] != null && tDate[1].matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d") ? StringUtils.toTextDate(tDate[1], lang, "dd MMM yyyy") : tDate[1]);
			tDate[2] = (tDate[2] != null && tDate[2].matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d") ? StringUtils.toTextDate(tDate[2], lang, "dd MMM yyyy") : tDate[2]);
			tDate[3] = (tDate[3] != null && tDate[3].matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d") ? StringUtils.toTextDate(tDate[3], lang, "dd MMM yyyy") : tDate[3]);
			tDate[4] = (tDate[4] != null && tDate[4].matches("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d") ? StringUtils.toTextDate(tDate[4], lang, "dd MMM yyyy") : tDate[4]);
			if (StringUtils.notEmpty(bean.getRcExa())) {
				List<Integer> lTie = StringUtils.tieList(bean.getRcExa());
				for (int i = 2 ; i <= 5 ; i++) {
					if (lTie.contains(i)) {
						tRank[0] += (!tRank[0].matches(".*\\<table\\>.*") ? "<br/>" : "") + tRank[i - 1];
						tRecord[0] += "<br/>" + tRecord[i - 1];
						tDate[0] += "<br/>" + tDate[i - 1];
					}
				}
			}

			// Write line
			html.append("<tr><td class='srt'>" + bean.getEvLabel() + "</td><td class='srt'>" + bean.getRcType1() + "</td><td class='srt'>" + bean.getRcType2() + "</td><td class='srt'>" + bean.getRcLabel() + "</td>");
			html.append("<td class='srt'><b>" + tRank[0] + "</b></td><td class='srt'><b>" + tRecord[0] + "</b></td>");
			html.append("<td class='srt'>" + tDate[0] + "</td>");
			html.append("</tr>");
		}
		html.append("</tbody></table>");
		return html;
	}

	public static StringBuffer convertWinLoss(Collection<Object> coll, String lang) throws Exception {
		if (coll == null || coll.isEmpty())
			return new StringBuffer(HtmlUtils.writeNoResult(lang));
		StringBuffer html = new StringBuffer("<table class='tsort'>");
		boolean isTie = false;
		boolean isOtloss = false;
		for (Object obj : coll) {
			WinLossBean bean = (WinLossBean) obj;
			isTie |= (bean.getWlCountTie() != null);
			isOtloss |= (bean.getWlCountOtloss() != null);
		}
		long id = System.currentTimeMillis();
		html.append("<tr class='rsort'><th onclick='sort(\"" + id + "\", this, 0);'>" + ResourceUtils.getText("team", lang) + "</th><th onclick='sort(\"" + id + "\", this, 1);'>" + ResourceUtils.getText("type", lang) + "</th><th onclick='sort(\"" + id + "\", this, 2);'>" + ResourceUtils.getText("wins", lang) + "</th><th onclick='sort(\"" + id + "\", this, 3);'>" + ResourceUtils.getText("losses", lang) + "</th>" + (isTie ? "<th onclick='sort(\"" + id + "\", this, 4);'>" + ResourceUtils.getText("ties", lang) + "</th>" : "") + (isOtloss ? "<th onclick='sort(\"" + id + "\", this, 5);'>" + ResourceUtils.getText("ties", lang) + "</th>" : "") + "<th onclick='sort(\"" + id + "\", this, " + (4 + (isTie ? 1 : 0) + (isOtloss ? 1 : 0)) + ");'>%</th></tr></thead><tbody id='tb-" + id + "'>");
		for (Object obj : coll) {
			WinLossBean bean = (WinLossBean) obj;

			// Evaluate bean
			String teamImg = HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getTmId(), ImageUtils.SIZE_SMALL, null, null);
			String team = HtmlUtils.writeLink(Team.alias, bean.getTmId(), bean.getTmLabel(), null);
			team = HtmlUtils.writeImgTable(teamImg, team);
			String stats = "<td class='srt'>" + bean.getWlCountWin() + "</td><td class='srt'>" + bean.getWlCountLoss() + "</td>";
			if (isTie)
				stats += "<td class='srt'>" + (bean.getWlCountTie() != null ? bean.getWlCountTie() : StringUtils.EMPTY) + "</td>";
			if (isOtloss)
				stats += "<td class='srt'>" + (bean.getWlCountOtloss() != null ? bean.getWlCountOtloss() : StringUtils.EMPTY) + "</td>";
			stats += "<td class='srt'>" + bean.getWlAverage() + "</td>";

			// Write line
			html.append("<tr><td class='srt'>" + team + "</td><td class='srt'>" + bean.getWlType() + "</td>" + stats + "</tr>");
		}
		html.append("</tbody></table>");
		return html;
	}
	
	public static StringBuffer convertLastUpdates(Collection<Object> coll, String lang) throws Exception {
		StringBuffer html = new StringBuffer("<table><tr><th>" + ResourceUtils.getText("year", lang) + "</th><th>" + ResourceUtils.getText("sport", lang) + "</th><th>" + ResourceUtils.getText("event", lang) + "</th><th>" + ResourceUtils.getText("entity.RS.1", lang) + "</th><th>" + ResourceUtils.getText("updated.on", lang) + "</th></tr>");
		for (Object obj : coll) {
			LastUpdateBean bean = (LastUpdateBean) obj;

			String pos1 = null;
			String pos2 = null;
			int number = (bean.getTp3Number() != null ? bean.getTp3Number() : (bean.getTp2Number() != null ? bean.getTp2Number() : bean.getTp1Number()));
			if (number < 10) {
				if (bean.getPr1LastName() != null) {
					pos1 = (StringUtils.notEmpty(bean.getPr1FirstName()) ? bean.getPr1FirstName().substring(0, 1) + "." : "") + bean.getPr1LastName();
					if (bean.getPr1Country() != null)
						pos1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getPr1Country(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Athlete.alias, bean.getPr1Id(), pos1, null));
				}
				if (bean.getPr2LastName() != null) {
					pos2 = (StringUtils.notEmpty(bean.getPr2FirstName()) ? bean.getPr2FirstName().substring(0, 1) + "." : "") + bean.getPr2LastName();
					if (bean.getPr2Country() != null)
						pos2 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getPr2Country(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Athlete.alias, bean.getPr2Id(), pos2, null));
				}
			}
			else if (number == 50 && bean.getTm1Id() != null) {
				pos1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getTm1Id(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Team.alias, bean.getTm1Id(), bean.getTm1Label(), null));
				if (bean.getTm2Id() != null)
					pos2 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_TEAM, bean.getTm2Id(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Team.alias, bean.getTm2Id(), bean.getTm2Label(), null));				
			}
			else if (number == 99 && bean.getCn1Id() != null) {
				pos1 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn1Id(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Country.alias, bean.getCn1Id(), bean.getCn1Label(), null));
				if (bean.getCn2Id() != null)
					pos2 = HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_COUNTRY, bean.getCn2Id(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Country.alias, bean.getCn2Id(), bean.getCn2Label(), null));
			}
			String update = new SimpleDateFormat("dd/MM/yyyy").format(bean.getRsUpdate());
			
			// Write line
			boolean isScore = (pos1 != null && pos2 != null && StringUtils.notEmpty(bean.getRsText1()) && !StringUtils.notEmpty(bean.getRsText2()));
			boolean isTie = (pos1 != null && pos2 != null && bean.getRsText3() != null && bean.getRsText3().matches("^1.*"));
			String link = "results?p=" + StringUtils.encode(bean.getSpId() + "-" + bean.getCpId() + "-" + bean.getEvId() + "-" + (bean.getSeId() != null ? bean.getSeId() : 0) + "-" + (bean.getSe2Id() != null ? bean.getSe2Id() : 0) + "-0");
			html.append("<tr><td><b>" + bean.getYrLabel() + "</b></td><td>" + HtmlUtils.writeImgTable(HtmlUtils.writeImage(ImageUtils.INDEX_SPORT, bean.getSpId(), ImageUtils.SIZE_SMALL, null, null), HtmlUtils.writeLink(Sport.alias, bean.getSpId(), bean.getSpLabel(), null)) + "</td><td><a href='" + link + "'>" + bean.getCpLabel() + "&nbsp;-&nbsp;" + bean.getEvLabel() + (StringUtils.notEmpty(bean.getSeLabel()) ? "&nbsp;-&nbsp;" + bean.getSeLabel() : "") + (StringUtils.notEmpty(bean.getSe2Label()) ? "&nbsp;-&nbsp;" + bean.getSe2Label() : "") + "</a></td>");
			html.append("<td>" + (isScore || isTie ? "<table><tr><td>" + pos1 + "</td><td>&nbsp;" + (isTie ? "/" : bean.getRsText1()) + "&nbsp;</td><td>" + pos2 + "</td></tr></table>" : (StringUtils.notEmpty(pos1) ? pos1 : "-")) + "</td>");
//			html.append("<td>" + (StringUtils.notEmpty(bean.getRsDate()) ? StringUtils.toTextDate(bean.getRsDate(), lang, "d MMM yyyy") : "-") + "</td>");
			html.append("<td>" + update + "</td></tr>");
		}
		return html.append("</table>");
	}
	
}