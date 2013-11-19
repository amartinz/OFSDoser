package net.openfiresecurity.ofsdoser.util.asynctasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import net.openfiresecurity.ofsdoser.R;
import net.openfiresecurity.ofsdoser.fragments.DosFragment;
import net.openfiresecurity.ofsdoser.util.PreferenceStorage;

/**
 * Validates the entered Target.
 */
public class TargetValidator extends AsyncTask<String, Integer, String> {

    private ProgressDialog dialog;
    private DosFragment mFragment;

    private void logDebug(String msg) {
        if (PreferenceStorage.EXTENSIVE_LOGGING) {
            Log.e("OFSDOSER", msg);
        }
    }

    /**
     * Constructor for the Validator
     *
     * @param fragment The DosFragment
     */
    public TargetValidator(DosFragment fragment) {
        mFragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mFragment.getActivity());
        dialog.setTitle(mFragment.getString(R.string.validator_validating));
        dialog.setMessage(mFragment.getString(R.string.validator_checking));
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                mFragment.getString(R.string.dialog_abort),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TargetValidator.this.cancel(true);
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        dialog.dismiss();
        mFragment.setValidationResult(s);
    }

    @Override
    protected String doInBackground(String... strings) {

        String target = strings[0];

        try {
            logDebug("Target not empty");
            target = target.trim();
            logDebug("Target: " + target);

            final String command = "ping -c 1 " + target;
            Process p1 = java.lang.Runtime.getRuntime().exec(command);
            int returnVal = p1.waitFor();

            if (returnVal == 0) {
                logDebug("Target is reachable!");
                if (URLUtil.isValidUrl("http://" + target + "/")) {
                    logDebug("Target is valid URL!");
                    return "1|" + target;
                }
            } else {
                logDebug("Target is not reachable!");
            }
        } catch (Exception exc) {
            logDebug("Error: " + exc.getLocalizedMessage());
        }
        return "0|" + target;
    }
}
