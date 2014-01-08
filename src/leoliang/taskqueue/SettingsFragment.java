package leoliang.taskqueue;

import leoliang.taskqueue.repository.SdCardBackupAgent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		Preference backup = findPreference("pref_key_backup_to_sd");
		backup.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					new SdCardBackupAgent(getActivity()).backup();
					Toast.makeText(getActivity(), R.string.toast_backup_done, Toast.LENGTH_SHORT)
							.show();
				} catch (SdCardBackupAgent.Error e) {
					Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});

		Preference restore = findPreference("pref_key_restore_from_sd");
		restore.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					new SdCardBackupAgent(getActivity()).restore();
					Toast.makeText(getActivity(), R.string.toast_restore_done, Toast.LENGTH_SHORT)
							.show();
				} catch (SdCardBackupAgent.Error e) {
					Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
	}
}
