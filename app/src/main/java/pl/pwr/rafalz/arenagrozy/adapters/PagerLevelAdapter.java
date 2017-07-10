package pl.pwr.rafalz.arenagrozy.adapters;

import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.data.DataBase;
import pl.pwr.rafalz.arenagrozy.view.GridView;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PagerLevelAdapter extends FragmentStatePagerAdapter {
	
	private static  int LEVELS_PER_SLIDE = 12;
	private static  int NUM_PAGES = (int)(DataBase.LEVELS_COUNT/ (float)LEVELS_PER_SLIDE + 0.99f);
	
	public PagerLevelAdapter(FragmentManager fm, Context context) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new DemoObjectFragment();
		Bundle args = new Bundle();
		//save page index
		args.putInt(DemoObjectFragment.ARG_OBJECT, i);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "OBJECT " + (position + 1);
	}

	/**
	 * single element in view - one page of levels
	 * @author Zientara
	 *
	 */
	public static class DemoObjectFragment extends Fragment {
		public static final String ARG_OBJECT = "object";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragmet_levels,
					container, false);
			Bundle args = getArguments();
			
			//load page index
			int i = args.getInt(ARG_OBJECT);
			
			int from= i*LEVELS_PER_SLIDE+1;
			int to = (i+1) * LEVELS_PER_SLIDE +1;
			if(to>DataBase.LEVELS_COUNT)
				to = DataBase.LEVELS_COUNT;
			GridView grid = (GridView)rootView.findViewById(R.id.levels_grid);
			grid.setLevels(DataBase.getLevelsProgress(from, to));
			
			//arrows
			if(i==0)//first page dont have left arrow
				rootView.findViewById(R.id.prev).setVisibility(View.INVISIBLE);
			
			if(from > DataBase.LEVELS_COUNT - LEVELS_PER_SLIDE)//first page dont have left arrow
				rootView.findViewById(R.id.next).setVisibility(View.INVISIBLE);
			
			return rootView;
		}
	}
}
