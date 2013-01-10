package net.sf.openrocket.appearance.defaults;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.sf.openrocket.appearance.DecalImage;


class ResourceDecalImage implements DecalImage {
	
	final String resource;
	
	ResourceDecalImage(final String resource) {
		this.resource = resource;
	}
	
	@Override
	public String getName() {
		return resource;
	}
	
	@Override
	public InputStream getBytes() throws FileNotFoundException, IOException {
		return this.getClass().getResourceAsStream(resource);
	}
	
	@Override
	public void exportImage(File file, boolean watchForChanges) throws IOException {
		
	}
	
}
