package com.sporthenon.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sporthenon.db.DatabaseHelper;
import com.sporthenon.db.PicklistBean;
import com.sporthenon.db.converter.HtmlConverter;
import com.sporthenon.utils.ExportUtils;
import com.sporthenon.utils.StringUtils;
import com.sporthenon.utils.res.ResourceUtils;

public class OlympicsServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	private static final String TYPE_SUMMER = "summer";
	//private static final String TYPE_WINTER = "winter";
	private static final String TYPE_INDIVIDUAL = "ind";
	private static final String TYPE_COUNTRY = "cnt";

	private static final String PICKLIST_ID_OLYMPICS = "pl-ol";
	private static final String PICKLIST_ID_COUNTRY = "pl-cn";

	public OlympicsServlet() {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			HashMap<String, Object> hParams = ServletHelper.getParams(request);
			if (hParams.containsKey("run")) { // View results
				boolean isLink = false;
				if (hParams.containsKey("p")) {
					String p = String.valueOf(hParams.get("p"));
					p = StringUtils.decode(p);
					String[] t = p.split("\\-");
					hParams.put("type", t[0]);
					hParams.put("ol", t[1]);
					if (t[0].equals(TYPE_INDIVIDUAL)) {
						hParams.put("sp", t[2]);
						hParams.put("ev", t[3]);
						hParams.put("se", t[4]);
						hParams.put("se2", t[5]);
					}
					else
						hParams.put("cn", t[2]);
					isLink = true;
				}
				ArrayList<Object> lFuncParams = new ArrayList<Object>();
				String type = String.valueOf(hParams.get("type"));
				StringBuffer html = null;
				if (type.equals(TYPE_INDIVIDUAL)) {
					lFuncParams.add(StringUtils.notEmpty(hParams.get("ol")) ? String.valueOf(hParams.get("ol")) : "0");
					lFuncParams.add(StringUtils.notEmpty(hParams.get("sp")) ? new Integer(String.valueOf(hParams.get("sp"))) : 0);
					lFuncParams.add(StringUtils.notEmpty(hParams.get("ev")) ? String.valueOf(hParams.get("ev")) : "0");
					lFuncParams.add(StringUtils.notEmpty(hParams.get("se")) ? String.valueOf(hParams.get("se")) : "0");
					lFuncParams.add(StringUtils.notEmpty(hParams.get("se2")) ? String.valueOf(hParams.get("se2")) : "0");
					lFuncParams.add("_" + getLocale(request));
					html = HtmlConverter.getHeader(HtmlConverter.HEADER_OLYMPICS_INDIVIDUAL, lFuncParams, getLocale(request));
					html.append(HtmlConverter.convertOlympicMedals(DatabaseHelper.call("GetOlympicMedals", lFuncParams), getLocale(request)));
				}
				else if (type.equals(TYPE_COUNTRY)) {
					lFuncParams.add(StringUtils.notEmpty(hParams.get("ol")) ? String.valueOf(hParams.get("ol")) : "0");
					lFuncParams.add(StringUtils.notEmpty(hParams.get("cn")) ? String.valueOf(hParams.get("cn")) : "0");
					lFuncParams.add("_" + getLocale(request));
					html = HtmlConverter.getHeader(HtmlConverter.HEADER_OLYMPICS_COUNTRY, lFuncParams, getLocale(request));
					html.append(HtmlConverter.convertOlympicRankings(DatabaseHelper.call("GetOlympicRankings", lFuncParams), getLocale(request)));
				}
				if (isLink) {
					if (hParams.containsKey("export"))
						ExportUtils.export(response, html, String.valueOf(hParams.get("export")));
					else
						ServletHelper.writePageHtml(request, response, html);
				}
				else
					ServletHelper.writeTabHtml(response, html, getLocale(request));
			}
			else if (hParams.containsKey("tree")) { // Tree of sports/events
				String ol = String.valueOf(hParams.get("ol"));
				String type = String.valueOf(hParams.get("type"));
				ArrayList<Object> lFuncParams = new ArrayList<Object>();
				lFuncParams.add("WHERE CP.id=1 AND SP.type=" + (type.equals(TYPE_SUMMER) ? 1 : 0) + (!ol.equals("0") ? " AND OL.id IN (" + ol + ")" : ""));
				lFuncParams.add("_" + getLocale(request));
				response.setCharacterEncoding("utf-8");
				HtmlConverter.convertTreeArray(DatabaseHelper.call("TreeResults", lFuncParams), response.getWriter());
			}
			else { // Picklists
				String lang = getLocale(request);
				String ol = String.valueOf(hParams.get("ol"));				
				String type = String.valueOf(hParams.get("type"));
				Collection<PicklistBean> cPicklist = new ArrayList<PicklistBean>();
				String plId = null;
				if (hParams.containsKey(PICKLIST_ID_COUNTRY)) {
					String sql = "SELECT DISTINCT CN.id, CN.label" + (lang != null && !lang.equalsIgnoreCase("en") ? "_" + lang : "") + " FROM \"OLYMPIC_RANKING\" OR_";
					sql += " LEFT JOIN \"COUNTRY\" CN ON OR_.id_country = CN.id";
					sql += (!ol.equals("0") ? " WHERE OR_.id_olympics IN (" + ol + ")" : "");
					sql += " ORDER BY CN.label" + (lang != null && !lang.equalsIgnoreCase("en") ? "_" + lang : "");
					cPicklist.add(new PicklistBean(0, "---&nbsp;" + ResourceUtils.getText("all.countries", getLocale(request)) + "&nbsp;---"));
					cPicklist.addAll(DatabaseHelper.getPicklistFromQuery(sql, true));
					plId = type + "-" + PICKLIST_ID_COUNTRY;
				}
				else {
					String hql = "select ol.id, concat(concat(ol.year.label, ' - '), ol.city.label" + (lang != null && !lang.equalsIgnoreCase("en") ? lang.toUpperCase() : "") + ") ";
					hql += " from Olympics ol where ol.type = " + (type.equals(TYPE_SUMMER) ? 1 : 0) + " order by ol.year.id desc";
					cPicklist.add(new PicklistBean(0, "---&nbsp;" + ResourceUtils.getText("all.olympic.games", getLocale(request)) + "&nbsp;---"));
					cPicklist.addAll(DatabaseHelper.getPicklistFromQuery(hql, false));
					plId = type + "-" + PICKLIST_ID_OLYMPICS;
				}
				ServletHelper.writePicklist(response, cPicklist, plId);
			}
		}
		catch (Exception e) {
			handleException(e);
		}
	}

}