package rfx.server.http.processor.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import rfx.server.http.BaseViewableModel;
import rfx.server.http.TemplateConfig;
import rfx.server.monitor.util.MemoryWatcher;

@TemplateConfig(location = "server-info")
public class ServerInfoModel extends BaseViewableModel {
	static final String classpath = ServerInfoModel.class.getName();
	String time;
	List<String> infos = new ArrayList<>();
	String filter;
	boolean showAll;
	boolean showCompact;
	String memoryStats;

	public ServerInfoModel(String filter) {
		this.filter = filter;		
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<String> getInfos() {
		return infos;
	}

	public void setInfos(List<String> infos) {
		this.infos = infos;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	public boolean isShowCompact() {
		return showCompact;
	}

	public void setShowCompact(boolean showCompact) {
		this.showCompact = showCompact;
	}

	public String getMemoryStats() {
		return memoryStats;
	}

	public void setMemoryStats(String memoryStats) {
		this.memoryStats = memoryStats;
	}

	@Override
	public void freeResource() {
		infos.clear();
	}

	@Override
	public BaseViewableModel prepareData() {
		time = new Date().toString();
		if (filter.equals("all")) {
			showAll = true;
			showCompact = false;
		} else if (filter.equals("compact")) {
			showAll = false;
			showCompact = true;
		}

		Properties props = System.getProperties();
		Enumeration<?> e = props.propertyNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement().toString();
			String s = (key + " : " + props.getProperty(key));
			if (showAll) {
				infos.add(s);
			} else if (showCompact) {
				{
					if (key.startsWith("java.vm")) {
						infos.add(s);
					}
				}
			}

		}
		memoryStats = MemoryWatcher.collectMemoryStats();
		return this;
	}

	@Override
	public String classpath() {
		return classpath;
	}
	
}
