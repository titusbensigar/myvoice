/**
 * 
 */
package vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ubensti
 *
 */
public class ChartVO {

	public Map<String, String> chartProperties = new HashMap<String, String>();
	
	public Map<String, String> getChartProperties() {
		if (chartProperties == null) return new HashMap<String, String>();
		return chartProperties;
	}
	public void setChartProperties(Map<String, String> chartProperties) {
		this.chartProperties = chartProperties;
	}
}
