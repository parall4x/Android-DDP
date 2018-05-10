package im.delight.android.ddp.examples;

/*
 * Copyright (c) delight.im <info@delight.im>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.ResultListener;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

public class SSLActivity extends Activity implements MeteorCallback {

	private Meteor mMeteor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ssl);

		// enable logging of internal events for the library
		Meteor.setLoggingEnabled(true);

		mMeteor = new Meteor(this, "https://testserver.localhost/websocket", new InMemoryDatabase());

		CertificateFactory cf;

		try {
			cf = CertificateFactory.getInstance("X.509");
			InputStream caInput = new BufferedInputStream(getResources().openRawResource(R.raw.ca));
			Certificate ca = cf.generateCertificate(caInput);
			mMeteor.trustCaCert(ca);
		} catch (CertificateException certException) {
			System.out.print("DDP-SSL: " + certException.getMessage());
		}

		// register the callback that will handle events and receive messages
		mMeteor.addCallback(this);

		// establish the connection
		mMeteor.connect();
	}

	@Override
	public void onConnect(final boolean signedInAutomatically) {
		Log.d("DDP-SSL", "Connected through SSL");
	}

	@Override
	public void onDisconnect() {
		Log.d("DDP-SSL", "Disconnected");
	}

	@Override
	public void onException(Exception e) {
		Log.d("DDP-SSL", "Connected through SSL");
		if (e != null) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		mMeteor.disconnect();
		mMeteor.removeCallback(this);
		// or
		// mMeteor.removeCallbacks();

		// ...

		super.onDestroy();
	}

	@Override
	public void onDataAdded(String collectionName, String documentID, String newValuesJson) {

	}

	@Override
	public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {

	}

	@Override
	public void onDataRemoved(String collectionName, String documentID) {

	}
}
