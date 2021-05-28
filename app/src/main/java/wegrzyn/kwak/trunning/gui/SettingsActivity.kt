package wegrzyn.kwak.trunning.gui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import wegrzyn.kwak.trunning.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val githubRepository = findPreference<Preference>("github_repository")
            githubRepository?.setOnPreferenceClickListener {
                val url = "https://github.com/piotrjwegrzyn/Trunning"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                true
            }

            val githubRepositoryKwak = findPreference<Preference>("github_repository_kwak")
            githubRepositoryKwak?.setOnPreferenceClickListener {
                val url = "https://github.com/Sirmarianus"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                true
            }

            val githubRepositoryWegrzyn = findPreference<Preference>("github_repository_wegrzyn")
            githubRepositoryWegrzyn?.setOnPreferenceClickListener {
                val url = "https://github.com/piotrjwegrzyn"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                true
            }

        }
    }
}
