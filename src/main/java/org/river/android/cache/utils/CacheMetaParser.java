package org.river.android.cache.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.river.android.cache.impl.CacheObject;
import org.river.android.cache.impl.HitBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * 缓存源文件解析工具类
 * @author river
 * @date 20130913
 */
public class CacheMetaParser {
	
	/**
	 * <p>
	 * 加载元数据
	 * 
	 * @throws Exception
	 */
	public static Map<String,CacheObject> loadMetaFile(File meta) throws Exception {
		Map<String,CacheObject> cachePool=new HashMap<String,CacheObject>();
		
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docBuilder.parse(meta);
		Node root = doc.getFirstChild();
		NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element item = (Element) nodeList.item(i);
			CacheObject obj = new CacheObject(item.getAttribute("key"), null);
			obj.setSize(Long.valueOf(item.getAttribute("size")));
			obj.setCreationTime(Long.valueOf(item.getAttribute("createTime")));
			obj.setHitCount(Long.valueOf(item.getAttribute("hitCount")));
			obj.setLastAccessTime(Long.valueOf(item.getAttribute("lastAccessTime")));
			obj.setLastUpdateTime(Long.valueOf(item.getAttribute("lastUpdateTime")));
			obj.setTimeToLive(Long.valueOf(item.getAttribute("timeToLive")));
			obj.setTimeToIdle(Long.valueOf(item.getAttribute("timeToIdle")));

			HitBean hit = new HitBean(obj.getKey());
			hit.setHitCount((int) obj.getHitCount());
			hit.setHitTime(parseTime(item.getAttribute("hitSerial")));
			obj.setHitBean(hit);
			
			cachePool.put((String)obj.getKey(), obj);
		}
		
		return cachePool;
	}

	/**
	 * <p>
	 * 解析击中时间序列
	 * 
	 * @param hitSerial
	 * @return
	 */
	public static List<Long> parseTime(String hitSerial) {
		String[] arr = hitSerial.split(",");
		if (arr == null || arr.length == 0) {
			return null;
		}

		List<Long> serial = new ArrayList<Long>();
		for (String tmp : arr) {
			if (tmp == null || tmp.isEmpty()) {
				continue;
			}

			serial.add(Long.valueOf(tmp));
		}
		return serial;
	}
	
	/**
	 * <p>
	 * 创建元数据xml
	 * 
	 * @return
	 */
	public static String buildMetaXml(Map<String,CacheObject> cachePool) {
		StringBuffer sb = new StringBuffer();
		sb.append("<meta>");

		Set<String> keys = cachePool.keySet();
		for (String tmp : keys) {
			sb.append("<item key=\"").append(tmp).append("\" ");
			sb.append(" size=\"").append(cachePool.get(tmp).getSize()).append("\" ");
			sb.append(" updateTime=\"").append(cachePool.get(tmp).getLastUpdateTime()).append("\" ");
			sb.append(" lastAccesTime=\"").append(cachePool.get(tmp).getLastAccessTime()).append("\" ");
			sb.append(" hitCount=\"").append(cachePool.get(tmp).getHitCount()).append("\" ");
			sb.append(" hitSerial=\"").append(cachePool.get(tmp).getHitBean().getTimeSerial()).append("\" ");
			sb.append(" timeToLive=\"").append(cachePool.get(tmp).getTimeToLive()).append("\" ");
			sb.append(" timeToIdle=\"").append(cachePool.get(tmp).getTimeToIdle()).append("\" ");

			sb.append("/>");
		}

		sb.append("</meta>");
		return sb.toString();
	}
}
