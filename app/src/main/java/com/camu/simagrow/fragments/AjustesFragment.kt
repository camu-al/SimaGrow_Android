package com.camu.simagrow.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.camu.simagrow.R
import com.camu.simagrow.activitis.MainActivity

class AjustesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        //Modo Oscuro
        val oscuro = findPreference<SwitchPreferenceCompat>("oscuro")

        oscuro?.setOnPreferenceChangeListener { _, newValue ->

            AppCompatDelegate.setDefaultNightMode(
                if (newValue as Boolean)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )

            // Recreacion de la activity
            requireActivity().recreate()
            true
        }// Fin modo oscuro

        // Musica
        val musica = findPreference<SwitchPreferenceCompat>("musica")

        musica?.setOnPreferenceChangeListener { _, newValue ->

            if (newValue as Boolean) {
                (activity as? MainActivity)?.iniciarMusica()
            } else {
                (activity as? MainActivity)?.pararMusica()
            }

            true
        }

    }
}
