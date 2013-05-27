package com.myosef.TabsTest;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {

	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		setContentView(mViewPager);
		
		final ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(bar.newTab().setText("Fragment 1"), Fragment_1.class, null);
		mTabsAdapter.addTab(bar.newTab().setText("Fragment 2"), Fragment_2.class, null);
		mTabsAdapter.addTab(bar.newTab().setText("Fragment 3"), Fragment_3.class, null);
		mTabsAdapter.addTab(bar.newTab().setText("Fragment 4"), Fragment_4.class, null);
	}
}
