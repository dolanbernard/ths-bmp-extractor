package us.macrohard.tektronix.thsBitmapExtractor.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import us.macrohard.tektronix.thsBitmapExtractor.uiShare.ExceptionDialogFactory;

public class BmpExConfiguration extends AbstractConfiguration {

	private static final long serialVersionUID = 1L;
	
	private static final String localCfgPath = System.getProperty("user.home").replace("\\", "/") + "/.tekBmpExtract/cfg";
	private static final String localCfgFile = localCfgPath + "/TekBmpExtract.cfg";
		
	private BmpExConfiguration() {
		super();
	}
	
	public void saveConfigurationToDisk() {
		StringBuilder b = new StringBuilder();
		try {
			for(String key : this.cfgMap.keySet()) {
				b.append(key).append('=').append(this.cfgMap.get(key)).append('\n');
				FileOutputStream fos = new FileOutputStream(localCfgFile, false);
				fos.write(b.toString().getBytes(StandardCharsets.UTF_8));
				fos.flush();
				fos.close();
				fos = null;
			}
		} catch(Exception e) {
			ExceptionDialogFactory.createErrorDialogForExceptionWithMessage(e, "<html>"
					+ "Unable to save configuration file<br />"
					+ "Click below to read the full error."
					+ "</html>", null);
		}
	}
	
	public static BmpExConfiguration readLocalConfiguration() {
		BmpExConfiguration config = null;
		try {
			config = new BmpExConfiguration();
			if(!(new File(localCfgFile).exists()))
				createDefaultConfiguration();
			Properties p = new Properties();
			p.load(new FileInputStream(localCfgFile));
			for(Object o : p.keySet()) {
				config.cfgMap.put(o.toString(), p.getProperty(o.toString()));
			}
			if(config.cfgMap.size() < EnumConfigurationKey.values().length) {
				for(EnumConfigurationKey cfgKey : EnumConfigurationKey.values()) {
					if(!config.cfgMap.containsKey(cfgKey.getKey())) {
						config.cfgMap.put(cfgKey.getKey(), cfgKey.getDefaultValue().toString());
						p.put(cfgKey.getKey(), cfgKey.getDefaultValue().toString());
					}
				}
				FileOutputStream fos = new FileOutputStream(localCfgFile, false);
				p.store(fos, "Sirius Configuration File");
				fos.close();
				fos = null;
			}
			p.clear();
			p = null;
		}catch(Exception e) {
			ExceptionDialogFactory.createErrorDialogForExceptionWithMessage(e, 
					"<html>Error reading configuration file. Empty Configuration will be used."
					+ "<br />Click below to read the full error.</html>", null);
			return new BmpExConfiguration();
		}
		return config;
	}
	
	private static void createDefaultConfiguration() {
		try {
			BmpExConfiguration config = new BmpExConfiguration();
			StringBuilder b = new StringBuilder();
			new File(localCfgPath).mkdirs();
			new File(localCfgFile).createNewFile();
			for(EnumConfigurationKey cfgKey : EnumConfigurationKey.values()) {
				config.cfgMap.put(cfgKey.getKey(), cfgKey.getDefaultValue().toString());
				b.append(cfgKey.getKey()).append('=').append(cfgKey.getDefaultValue().toString()).append('\n');
			}
			FileOutputStream fos = new FileOutputStream(localCfgFile, false);
			fos.write(b.toString().getBytes(StandardCharsets.UTF_8));
			fos.flush();
			fos.close();
			fos = null;
			config.cfgMap.clear();
			config = null;
		} catch(Exception e) {
			ExceptionDialogFactory.createErrorDialogForExceptionWithMessage(e, 
					"<html>Error creating default configuration file."
					+ "<br />Click below to read the full error.</html>", null);
		}
	}
	
}
