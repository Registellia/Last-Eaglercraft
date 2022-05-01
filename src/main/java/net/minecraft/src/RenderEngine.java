package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.lax1dude.eaglercraft.EaglerImage;
import net.lax1dude.eaglercraft.TextureLocation;

public class RenderEngine {

	public RenderEngine(TexturePackList texturepacklist, GameSettings gamesettings) {
		textureMap = new HashMap();
		textureNameToImageMap = new HashMap();
		singleIntBuffer = GLAllocation.createDirectIntBuffer(1);
		imageDataA = GLAllocation.createDirectByteBuffer(0x100000);
		imageDataB1 = GLAllocation.createDirectByteBuffer(0x100000);
		imageDataB2 = GLAllocation.createDirectByteBuffer(0x100000);
		textureList = new ArrayList();
		urlToImageDataMap = new HashMap();
		clampTexture = false;
		blurTexture = false;
		field_6527_k = texturepacklist;
		options = gamesettings;
	}

	public int getTexture(String s) {
		TexturePackBase texturepackbase = field_6527_k.selectedTexturePack;
		Integer integer = (Integer) textureMap.get(s);
		if (integer != null) {
			return integer.intValue();
		}
		try {
			singleIntBuffer.clear();
			GLAllocation.generateTextureNames(singleIntBuffer);
			int i = singleIntBuffer.get(0);
			//if (s.startsWith("##")) {
			//	setupTexture(unwrapImageByColumns(readTextureImage(texturepackbase.func_6481_a(s.substring(2)))), i);
			//} else 
			if (s.startsWith("%clamp%")) {
				clampTexture = true;
				setupTexture(readTextureImage(texturepackbase.func_6481_a(s.substring(7))), i);
				clampTexture = false;
			} else if (s.startsWith("%blur%")) {
				blurTexture = true;
				setupTexture(readTextureImage(texturepackbase.func_6481_a(s.substring(6))), i);
				blurTexture = false;
			} else {
				if(s.equals("/terrain.png")) {
					useMipmaps = true;
				}
				setupTexture(readTextureImage(texturepackbase.func_6481_a(s)), i);
				useMipmaps = false;
			}
			textureMap.put(s, Integer.valueOf(i));
			return i;
		} catch (IOException ioexception) {
			throw new RuntimeException("!!");
		}
	}
/*
	private BufferedImage unwrapImageByColumns(BufferedImage bufferedimage) {
		int i = bufferedimage.getWidth() / 16;
		BufferedImage bufferedimage1 = new BufferedImage(16, bufferedimage.getHeight() * i, 2);
		Graphics g = bufferedimage1.getGraphics();
		for (int j = 0; j < i; j++) {
			g.drawImage(bufferedimage, -j * 16, j * bufferedimage.getHeight(), null);
		}

		g.dispose();
		return bufferedimage1;
	}
*/
	public int allocateAndSetupTexture(EaglerImage bufferedimage) {
		singleIntBuffer.clear();
		GLAllocation.generateTextureNames(singleIntBuffer);
		int i = singleIntBuffer.get(0);
		setupTexture(bufferedimage, i);
		textureNameToImageMap.put(Integer.valueOf(i), bufferedimage);
		return i;
	}

	public void setupTexture(EaglerImage bufferedimage, int i) {
		EaglerAdapter.glBindTexture(3553 /* GL_TEXTURE_2D */, i);
		if (useMipmaps) {
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10241 /* GL_TEXTURE_MIN_FILTER */, EaglerAdapter.GL_NEAREST_MIPMAP_LINEAR);
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10240 /* GL_TEXTURE_MAG_FILTER */, EaglerAdapter.GL_NEAREST /* GL_LINEAR */);
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, EaglerAdapter.GL_TEXTURE_MAX_LEVEL, 4);
		} else {
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10241 /* GL_TEXTURE_MIN_FILTER */, 9728 /* GL_NEAREST */);
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10240 /* GL_TEXTURE_MAG_FILTER */, 9728 /* GL_NEAREST */);
		}
		if (blurTexture) {
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10241 /* GL_TEXTURE_MIN_FILTER */, 9729 /* GL_LINEAR */);
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10240 /* GL_TEXTURE_MAG_FILTER */, 9729 /* GL_LINEAR */);
		}
		if (clampTexture) {
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10242 /* GL_TEXTURE_WRAP_S */, 10496 /* GL_CLAMP */);
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10243 /* GL_TEXTURE_WRAP_T */, 10496 /* GL_CLAMP */);
		} else {
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10242 /* GL_TEXTURE_WRAP_S */, 10497 /* GL_REPEAT */);
			EaglerAdapter.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10243 /* GL_TEXTURE_WRAP_T */, 10497 /* GL_REPEAT */);
		}
		int j = bufferedimage.w;
		int k = bufferedimage.h;
		int ai[] = bufferedimage.data;
		byte abyte0[] = new byte[j * k * 4];
		for (int l = 0; l < ai.length; l++) {
			int j1 = ai[l] >> 24 & 0xff;
			int l1 = ai[l] >> 16 & 0xff;
			int j2 = ai[l] >> 8 & 0xff;
			int l2 = ai[l] >> 0 & 0xff;
			if (options != null && options.anaglyph) {
				int j3 = (l1 * 30 + j2 * 59 + l2 * 11) / 100;
				int l3 = (l1 * 30 + j2 * 70) / 100;
				int j4 = (l1 * 30 + l2 * 70) / 100;
				l1 = j3;
				j2 = l3;
				l2 = j4;
			}
			abyte0[l * 4 + 0] = (byte) l1;
			abyte0[l * 4 + 1] = (byte) j2;
			abyte0[l * 4 + 2] = (byte) l2;
			abyte0[l * 4 + 3] = (byte) j1;
		}
		imageDataB1.clear();
		imageDataB1.put(abyte0);
		imageDataB1.position(0).limit(abyte0.length);
		EaglerAdapter.glTexImage2D(3553 /* GL_TEXTURE_2D */, 0, 6408 /* GL_RGBA */, j, k, 0, 6408 /* GL_RGBA */,
				5121 /* GL_UNSIGNED_BYTE */, imageDataB1);
		if (useMipmaps) {
			for (int i1 = 1; i1 <= 4; i1++) {
				int k1 = j >> i1 - 1;
				int i2 = j >> i1;
				int k2 = k >> i1;
				imageDataB2.clear();
				for (int i3 = 0; i3 < i2; i3++) {
					for (int k3 = 0; k3 < k2; k3++) {
						int i4 = imageDataB1.getInt((i3 * 2 + 0 + (k3 * 2 + 0) * k1) * 4);
						int k4 = imageDataB1.getInt((i3 * 2 + 1 + (k3 * 2 + 0) * k1) * 4);
						int l4 = imageDataB1.getInt((i3 * 2 + 1 + (k3 * 2 + 1) * k1) * 4);
						int i5 = imageDataB1.getInt((i3 * 2 + 0 + (k3 * 2 + 1) * k1) * 4);
						int j5 = averageColor(averageColor(i4, k4), averageColor(l4, i5));
						imageDataB2.putInt((i3 + k3 * i2) * 4, j5);
					}

				}
				
				EaglerAdapter.glTexImage2D(3553 /* GL_TEXTURE_2D */, i1, 6408 /* GL_RGBA */, i2, k2, 0, 6408 /* GL_RGBA */,
						5121 /* GL_UNSIGNED_BYTE */, imageDataB2);
				ByteBuffer tmp = imageDataB1;
				imageDataB1 = imageDataB2;
				imageDataB2 = tmp;
			}

		}
	}

	public void deleteTexture(int i) {
		EaglerAdapter.glDeleteTextures(i);
	}

	public int getTextureForDownloadableImage(String s, String s1) {
		/*
		ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) urlToImageDataMap.get(s);
		if (threaddownloadimagedata != null && threaddownloadimagedata.image != null
				&& !threaddownloadimagedata.textureSetupComplete) {
			if (threaddownloadimagedata.textureName < 0) {
				threaddownloadimagedata.textureName = allocateAndSetupTexture(threaddownloadimagedata.image);
			} else {
				setupTexture(threaddownloadimagedata.image, threaddownloadimagedata.textureName);
			}
			threaddownloadimagedata.textureSetupComplete = true;
		}
		if (threaddownloadimagedata == null || threaddownloadimagedata.textureName < 0) {
			if (s1 == null) {
				return getTexture("/mob/char.png");
			} else {
				return getTexture(s1);
			}
		} else {
			return threaddownloadimagedata.textureName;
		}
		*/
		return getTexture("/mob/char.png");
	}
/*
	public ThreadDownloadImageData obtainImageData(String s, ImageBuffer imagebuffer) {
		ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) urlToImageDataMap.get(s);
		if (threaddownloadimagedata == null) {
			urlToImageDataMap.put(s, new ThreadDownloadImageData(s, imagebuffer));
		} else {
			threaddownloadimagedata.referenceCount++;
		}
		return threaddownloadimagedata;
	}

	public void releaseImageData(String s) {
		ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) urlToImageDataMap.get(s);
		if (threaddownloadimagedata != null) {
			threaddownloadimagedata.referenceCount--;
			if (threaddownloadimagedata.referenceCount == 0) {
				if (threaddownloadimagedata.textureName >= 0) {
					deleteTexture(threaddownloadimagedata.textureName);
				}
				urlToImageDataMap.remove(s);
			}
		}
	}
*/
	public void registerTextureFX(TextureFX texturefx) {
		textureList.add(texturefx);
		texturefx.onTick();
	}

	public void updateTerrainTextures() {
		for (int i = 0; i < textureList.size(); i++) {
			TextureFX texturefx = (TextureFX) textureList.get(i);
			texturefx.anaglyphEnabled = options.anaglyph;
			texturefx.onTick();
			int tileSize = 16 * 16 * 4;
			imageDataA.clear();
			imageDataA.put(texturefx.imageData);
			imageDataA.position(0).limit(tileSize);
			texturefx.bindImage(this);
			
			imageDataA.position(0).limit(tileSize);

			for (int k = 0; k < texturefx.tileSize; k++) {
				for (int i1 = 0; i1 < texturefx.tileSize; i1++) {
					int idx = texturefx.iconIndex + k + i1 * 16;
					imageDataA.mark();
					EaglerAdapter.glTexSubImage2D(3553 /* GL_TEXTURE_2D */, 0, (idx % 16) * 16, (idx / 16) * 16, 16, 16, 6408 /* GL_RGBA */,
							5121 /* GL_UNSIGNED_BYTE */, imageDataA);
					imageDataA.rewind();
				}
			}
			
			if(texturefx.tileImage == 0) {
				imageDataA.position(0).limit(tileSize);
				imageDataB1.clear();
				imageDataB1.put(imageDataA);
				imageDataB1.flip();
				int k1 = 1;
				do {
					if (k1 > 4) {
						break;
					}
					int i2 = 16 >> k1 - 1;
					int k2 = 16 >> k1;
					imageDataB2.clear();
					for (int i3 = 0; i3 < k2; i3++) {
						for (int k3 = 0; k3 < k2; k3++) {
							int i4 = imageDataB1.getInt((i3 * 2 + 0 + (k3 * 2 + 0) * i2) * 4);
							int k4 = imageDataB1.getInt((i3 * 2 + 1 + (k3 * 2 + 0) * i2) * 4);
							int i5 = imageDataB1.getInt((i3 * 2 + 1 + (k3 * 2 + 1) * i2) * 4);
							int k5 = imageDataB1.getInt((i3 * 2 + 0 + (k3 * 2 + 1) * i2) * 4);
							int l5 = averageColor(averageColor(i4, k4), averageColor(i5, k5));
							imageDataB2.putInt((i3 + k3 * k2) * 4, l5);
						}
					}
					
					for (int k = 0; k < texturefx.tileSize; k++) {
						for (int i1 = 0; i1 < texturefx.tileSize; i1++) {
							int idx = texturefx.iconIndex + k + i1 * 16;
							imageDataB2.mark();
							EaglerAdapter.glTexSubImage2D(3553 /* GL_TEXTURE_2D */, k1, (idx % 16) * k2, (idx / 16) * k2, k2, k2, 6408 /* GL_RGBA */,
									5121 /* GL_UNSIGNED_BYTE */, imageDataB2);
							imageDataB2.rewind();
						}
					}
					
					k1++;
					ByteBuffer tmp = imageDataB1;
					imageDataB1 = imageDataB2;
					imageDataB2 = tmp;
				} while (true);
			}

		}

	}

	private int averageColor(int i, int j) {
		int k = (i & 0xff000000) >> 24 & 0xff;
		int l = (j & 0xff000000) >> 24 & 0xff;
		return ((k + l >> 1) << 24) + ((i & 0xfefefe) + (j & 0xfefefe) >> 1);
		
	}
/*
	private int weightedAverageColor(int i, int j) {
		int k = (i & 0xff000000) >> 24 & 0xff;
		int l = (j & 0xff000000) >> 24 & 0xff;
		char c = '\377';
		if (k + l == 0) {
			k = 1;
			l = 1;
			c = '\0';
		}
		int i1 = (i >> 16 & 0xff) * k;
		int j1 = (i >> 8 & 0xff) * k;
		int k1 = (i & 0xff) * k;
		int l1 = (j >> 16 & 0xff) * l;
		int i2 = (j >> 8 & 0xff) * l;
		int j2 = (j & 0xff) * l;
		int k2 = (i1 + l1) / (k + l);
		int l2 = (j1 + i2) / (k + l);
		int i3 = (k1 + j2) / (k + l);
		return c << 24 | k2 << 16 | l2 << 8 | i3;
	}
*/
	public void refreshTextures() {
		TextureLocation.freeTextures();
		TexturePackBase texturepackbase = field_6527_k.selectedTexturePack;
		int i;
		EaglerImage bufferedimage;
		for (Iterator iterator = textureNameToImageMap.keySet().iterator(); iterator
				.hasNext(); setupTexture(bufferedimage, i)) {
			i = ((Integer) iterator.next()).intValue();
			bufferedimage = (EaglerImage) textureNameToImageMap.get(Integer.valueOf(i));
		}

		//for (Iterator iterator1 = urlToImageDataMap.values().iterator(); iterator1.hasNext();) {
		//	ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) iterator1.next();
		//	threaddownloadimagedata.textureSetupComplete = false;
		//}

		for (Iterator iterator2 = textureMap.keySet().iterator(); iterator2.hasNext();) {
			String s = (String) iterator2.next();
			try {
				EaglerImage bufferedimage1;
				/*
				if (s.startsWith("##")) {
					bufferedimage1 = unwrapImageByColumns(
							readTextureImage(texturepackbase.func_6481_a(s.substring(2))));
				} else
				*/
				if (s.startsWith("%clamp%")) {
					clampTexture = true;
					bufferedimage1 = readTextureImage(texturepackbase.func_6481_a(s.substring(7)));
				} else if (s.startsWith("%blur%")) {
					blurTexture = true;
					bufferedimage1 = readTextureImage(texturepackbase.func_6481_a(s.substring(6)));
				} else {
					bufferedimage1 = readTextureImage(texturepackbase.func_6481_a(s));
				}
				int j = ((Integer) textureMap.get(s)).intValue();
				setupTexture(bufferedimage1, j);
				blurTexture = false;
				clampTexture = false;
			} catch (IOException ioexception) {
				ioexception.printStackTrace();
			}
		}

	}

	private EaglerImage readTextureImage(byte[] inputstream) throws IOException {
		return EaglerImage.loadImage(inputstream);
	}

	public void bindTexture(int i) {
		if (i < 0) {
			return;
		} else {
			EaglerAdapter.glBindTexture(3553 /* GL_TEXTURE_2D */, i);
			return;
		}
	}

	public static boolean useMipmaps = false;
	private HashMap textureMap;
	private HashMap textureNameToImageMap;
	private IntBuffer singleIntBuffer;
	private ByteBuffer imageDataA;
	private ByteBuffer imageDataB1;
	private ByteBuffer imageDataB2;
	private java.util.List textureList;
	private Map urlToImageDataMap;
	private GameSettings options;
	private boolean clampTexture;
	private boolean blurTexture;
	private TexturePackList field_6527_k;

}
