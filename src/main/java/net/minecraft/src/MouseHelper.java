package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.lax1dude.eaglercraft.EaglerAdapter;

public class MouseHelper {

	public void func_774_a() {
		EaglerAdapter.mouseSetGrabbed(true);
		deltaX = 0;
		deltaY = 0;
	}

	public void func_773_b() {
		EaglerAdapter.mouseSetCursorPosition(EaglerAdapter.getCanvasWidth() / 2, EaglerAdapter.getCanvasHeight() / 2);
		EaglerAdapter.mouseSetGrabbed(false);
	}

	public void mouseXYChange() {
		deltaX = EaglerAdapter.mouseGetDX();
		deltaY = EaglerAdapter.mouseGetDY();
	}
	
	public int deltaX;
	public int deltaY;
}
