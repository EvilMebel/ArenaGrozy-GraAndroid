package pl.pwr.rafalz.arenagrozy.adapters;

import java.util.List;

import pl.pwr.rafalz.arenagrozy.MenuMusic;
import pl.pwr.rafalz.arenagrozy.R;
import pl.pwr.rafalz.arenagrozy.StatsActivity;
import pl.pwr.rafalz.arenagrozy.data.DataBase;
import pl.pwr.rafalz.arenagrozy.view.LevelBar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class StatsListAdapter extends ArrayAdapter<Integer> {
	private final Context context;
	private final List<StatsSkillFrame> values;// lista z indeksami wizyt
	private int whichHero;

	public StatsListAdapter(Context context, List<StatsSkillFrame> docs, int whichHero) {
		super(context, R.layout.stats_item);
		this.context = context;
		this.values = docs;
		this.whichHero = whichHero;
	}
	
	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final StatsSkillFrame stat = values.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.stats_item, parent,
				false);

		//prepare views
		TextView stat_name = (TextView) rowView.findViewById(R.id.stat_name);
		final TextView stat_cost = (TextView) rowView.findViewById(R.id.stat_cost);
		final TextView stat_discr = (TextView) rowView.findViewById(R.id.stat_discr);
		final LevelBar stat_level = (LevelBar) rowView.findViewById(R.id.stat_level);
		final Button stat_add = (Button) rowView.findViewById(R.id.stat_add);
		
		//name of stat
		stat_name.setText(stat.getNameId());
		
		//cost of skill, or show max status
		String cost;
		if(stat.getCurrent_level() < stat.getMax_level()) {
			cost = context.getResources().getString(R.string.stats_cost)+ " " 
					+ Integer.toString(stat.getCost()[stat.getCurrent_level()]);
		} else {
			cost = context.getResources().getString(R.string.stats_max_level);
			stat_add.setVisibility(View.INVISIBLE);
		}
		stat_cost.setText(cost);
		
		//prepare status bar
		stat_level.setMaxLevel(stat.getMax_level());
		stat_level.setCurrentLevel(stat.getCurrent_level());
		stat_discr.setText(stat.getHelpId());
		stat_add.setEnabled(hasEnoughExp(stat));
		
		//level up
		stat_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addLevel(stat_add, stat_cost, stat_level, stat, position);
				MenuMusic.playButtonSound(getContext(), R.raw.success1);
			}
		});

		return rowView;
	}
	
	/**
	 * upgrade stat and refresh list
	 * @param button
	 * @param cost_text
	 * @param bar
	 * @param stat
	 * @param position
	 */
	private void addLevel(Button button, TextView cost_text, LevelBar bar,  StatsSkillFrame stat, int position) {
		if(hasEnoughExp(stat)){
			StatsActivity sa = ((StatsActivity)context);
			sa.saveScrollPosition();
			
			int exp = DataBase.getHeroExp(whichHero);
			int cost = stat.getCostForNextLevel();
			int c_lvl = stat.getCurrent_level();
			bar.addLevel();
			DataBase.setHeroExp(whichHero, exp-cost);
			DataBase.setHeroLvL(whichHero,c_lvl+1, position);
			stat.setCurrent_level(c_lvl+1);
			
			notifyDataSetChanged();
			
			sa.updateExp(whichHero);
			sa.resumeScrollPosition();
			
		}
	}
	
	/**
	 * hero have enough exp for next level?
	 * @param stat
	 * @return
	 */
	private boolean hasEnoughExp(StatsSkillFrame stat) {
		int exp = DataBase.getHeroExp(whichHero);
		int cost = stat.getCostForNextLevel();
		
		if(cost<=exp && cost != -1){
			return true;
		}
		return false;
		
	}

}
