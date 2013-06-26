package com.desipal.Librerias;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.desipal.eventu.Fragment1;
import com.desipal.eventu.Fragment2;
import com.viewpagerindicator.IconPagerAdapter;

public class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	protected static final String[] CONTENT = new String[] { "Inicio", "Cerca de mí" };

	private int mCount = CONTENT.length;

	public TestFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = new Fragment1();
		switch (position) {
		case 0:
			fragment = new Fragment1();
			break;
		case 1:
			fragment = new Fragment2();
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		String title = "";
		switch (position) {
		case 0:
			title = "Inicio";
			break;
		case 1:
			title = "Cerca de mí";
			break;
		}

		return title;
	}

	public void setCount(int count) {
		if (count > 0 && count < 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}

}
