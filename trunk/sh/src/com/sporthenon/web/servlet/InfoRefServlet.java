package com.sporthenon.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sporthenon.db.DatabaseHelper;
import com.sporthenon.db.converter.HtmlConverter;
import com.sporthenon.db.entity.Draw;
import com.sporthenon.db.entity.Result;
import com.sporthenon.utils.ExportUtils;
import com.sporthenon.utils.HtmlUtils;
import com.sporthenon.utils.StringUtils;

public class InfoRefServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	public InfoRefServlet() {
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			HashMap<String, Object> hParams = ServletHelper.getParams(request);
			String[] params = StringUtils.decode(String.valueOf(hParams.get("p"))).split("-");
			StringBuffer html = new StringBuffer();
			boolean isLink = hParams.containsKey("run");
			boolean isExport = hParams.containsKey("export");
			boolean isDraw = params[0].equals(Draw.alias);
			boolean isResult = params[0].equals(Result.alias);
			
			if (isResult) {
				String p = "";
				if (params.length == 2) {
					Result rs = (Result) DatabaseHelper.loadEntity(Result.class, new Integer(params[1]));
					p = rs.getSport().getId() + "-" + rs.getChampionship().getId() + "-" + rs.getEvent().getId() + "-" + (rs.getSubevent() != null ? rs.getSubevent().getId() : "") + "-" + (rs.getSubevent2() != null ? rs.getSubevent2().getId() : "") + "-0";
				}
				else
					p = params[1] + "-" + params[2] + "-" + params[3] + "-" + (params.length > 4 ? params[4] : "") + "-" + (params.length > 5 ? params[5] : "") + "-0";
				response.sendRedirect("/results?p=" + StringUtils.encode(p));
			}
			else {
				ArrayList<Object> lFuncParams = new ArrayList<Object>();
				lFuncParams.add(params[0]);
				lFuncParams.add(new Integer(params[1]));
				lFuncParams.add(params.length > 2 ? params[2] : "");
				lFuncParams.add("_" + getLocale(request));
				
				// Info
				if (params.length == 2) {
					StringBuffer sbRecordInfo = HtmlConverter.getRecordInfo(params[0], new Integer(params[1]), getLocale(request));
					lFuncParams.add(sbRecordInfo.toString().replaceAll("\\</span\\>.*", "").replaceAll(".*title'\\>", ""));
					html.append(HtmlConverter.getHeader(HtmlConverter.HEADER_REF, lFuncParams, getLocale(request)));
					html.append(sbRecordInfo);
					lFuncParams.remove(4);
				}
				
				// References
				if (!isDraw)
					html.append(HtmlConverter.getRecordRef(lFuncParams, DatabaseHelper.call("EntityRef", lFuncParams), isExport, getLocale(request)));
				
				if (isLink) {
					HtmlUtils.setTitle(request, html.toString());
					if (isExport)
						ExportUtils.export(response, html, String.valueOf(hParams.get("export")));
					else
						ServletHelper.writePageHtml(request, response, html, hParams.containsKey("print"));
				}
				else
					ServletHelper.writeTabHtml(response, html, getLocale(request));				
			}
		}
		catch (Exception e) {
			handleException(e);
		}
	}

}