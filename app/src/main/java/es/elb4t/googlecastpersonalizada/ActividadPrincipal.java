package es.elb4t.googlecastpersonalizada;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

public class ActividadPrincipal extends AppCompatActivity {
    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private Button textoButton;
    private Button fondoButton;
    CanalCast mCanalCast = new CanalCast();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CastContext castContext = CastContext.getSharedInstance(this);
        mSessionManager = castContext.getSessionManager();
        textoButton = (Button) findViewById(R.id.btn_texto);
        textoButton.setOnClickListener(btnClickListener);
        fondoButton = (Button) findViewById(R.id.btn_fondo);
        fondoButton.setOnClickListener(btnClickListener);
    }

    private final View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_texto:
                    sendMessage("#T#hola");
                    Log.e("CAST", "-----BONON TEXTO");
                    break;
                case R.id.btn_fondo:
                    sendMessage("#F#blue");
                    Log.e("CAST", "-----BONON FONDO");
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.media_route_menu_item) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();

    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarted(Session session, String sessionId) {
            invalidateOptionsMenu();
            setSessionStarted(true);
            mCastSession = mSessionManager.getCurrentCastSession();
        }

        @Override
        public void onSessionResumed(Session session, boolean wasSuspended) {
            invalidateOptionsMenu();
            setSessionStarted(true);
        }

        @Override
        public void onSessionSuspended(Session session, int error) {
            setSessionStarted(false);
        }

        @Override
        public void onSessionStarting(Session session) {
        }

        @Override
        public void onSessionResuming(Session session, String sessionId) {
        }

        @Override
        public void onSessionStartFailed(Session session, int error) {
        }

        @Override
        public void onSessionResumeFailed(Session session, int error) {
        }

        @Override
        public void onSessionEnding(Session session) {
        }

        @Override
        public void onSessionEnded(Session session, int error) {
            setSessionStarted(false);
        }
    }

    private void setSessionStarted(boolean enabled) {
        textoButton.setEnabled(enabled);
        fondoButton.setEnabled(enabled);
        if (mCastSession != null && mCanalCast == null) {
            mCanalCast = new CanalCast();
            try {
                mCastSession.setMessageReceivedCallbacks(mCanalCast.getNamespace(), mCanalCast);
            } catch (IOException e) {
                mCanalCast = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSessionManager.addSessionManagerListener(mSessionManagerListener);
        mCastSession = mSessionManager.getCurrentCastSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;
    }

    private void sendMessage(String message) {
        if (mCanalCast != null) {
            try {
                mCastSession.sendMessage(mCanalCast.getNamespace(), message)
                        .setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status result) {
                                        if (!result.isSuccess()) {
                                            Toast.makeText(getApplicationContext(), "Error al enviar el mensaje.", Toast.LENGTH_LONG);
                                        }
                                    }
                                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext()
                        , "Error al enviar el mensaje: " + e, Toast.LENGTH_LONG);
            }
        }
    }


    class CanalCast implements Cast.MessageReceivedCallback {
        public String getNamespace() {
            return "urn:x-cast:es.elb4t.googlecastpersonalizada";
        }

        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        }
    }
}