package freakr.streams.apps;

import java.util.ArrayList;
import java.util.List;

import mei.app.streams.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import freakr.streams_lib.apps.Client;
import freakr.streams_lib.apps.Datenbank;
import freakr.streams_lib.apps.Setup_Client_Android;
import freakr.streams_lib.apps.Streams_lib;

public class MainActivity extends ActionBarActivity implements Streams_lib {

	//Main Class
	static boolean threadDBUStarted = false;
	MyNotification MyNotify = new MyNotification(this);
	static List<List> gesamtliste = new ArrayList();
	static ArrayList<String> spinnerserie = new ArrayList();
	static ArrayList<String> spinnerstaffeln = new ArrayList();
	static ArrayList<String> spinnerepisoden = new ArrayList();
	static String head = null;
	static int days;
	FragmentManager fm = getSupportFragmentManager();
	static Context context;

	final static Setup_Client_Android setup = new Setup_Client_Android();

	void set_liste(final String x) {
		android.support.v4.app.FragmentTransaction transaction = fm
				.beginTransaction();
		switch (x) {
		case ALLE_FOLGEN:
			head = ALLE_FOLGEN;
			spinnerserie = Datenbank.ausgabe_serien_all();
			break;
		case NEUE_FOLGEN:
			head = NEUE_FOLGEN;
			spinnerserie = Datenbank.ausgabe_serien_neu(days);
			break;
		case NOCH_NICHT_GESEHEN_FOLGEN:
			head = NOCH_NICHT_GESEHEN_FOLGEN;
			spinnerserie = Datenbank.ausgabe_serien_ungesehen();
			break;
		default:
			break;
		}
		transaction.disallowAddToBackStack();
		if (fm.getFragments() != null) {
			transaction.remove(fm.findFragmentByTag(Messages
					.getString("MainActivity.6")));
		}
		transaction.replace(R.id.container, new Serien_ListFragment(),
				Messages.getString("MainActivity.6")); //$NON-NLS-1$
		transaction.commit();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread t = Thread.currentThread();
		t.setName("Main Thread");
		Datenbank DB = new Datenbank();
		DB.initialisiern_datenbank();
		context = getApplicationContext();
		setup.Create(getApplicationContext());
		String ip = null;
		String ipnet = null;
		String ipwifi = null;
		try {
			ipwifi = new NetTaskLocalIPWifi(context).execute().get();
			ipnet = new NetTaskLocalIP().execute().get();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (ipnet != null) {
			ip = ipnet;
		}
		if (ipwifi != null) {
			ip = ipwifi;
		}
		setup.set_Parameter(Streams_lib.PARAMETER_IP,
				(String.valueOf(ip)));
		days = Integer.parseInt(setup
				.get_Parameter(Streams_lib.PARAMETER_NEU_TAGE));
		setContentView(R.layout.activity_main);
		String start = setup
				.get_Parameter(Streams_lib.PARAMETER_WINDOW);
		set_liste(start);
		new Thread(new Client(Streams_lib.HOST,Streams_lib.DB_UPDATE_FULL, null)).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		new MenuInflater(this).inflate(R.menu.main, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_einstellungen:
			android.support.v4.app.FragmentTransaction transaction = fm
					.beginTransaction();
			head = EINSTELLUNGEN;
			transaction.disallowAddToBackStack();
			if (fm.getFragments() != null) {
				transaction.remove(fm.findFragmentByTag(Messages
						.getString("MainActivity.6")));
			}
			transaction.replace(R.id.container, new EinstellungFragment(),
					Messages.getString("MainActivity.6")); //$NON-NLS-1$
			transaction.commit();

			return true;
		case R.id.action_dbupdate:

			if (!threadDBUStarted) {

				Thread DBUpdate = new Thread(new MakeUpdate(
						getApplicationContext(), threadDBUStarted));
				DBUpdate.start();
				threadDBUStarted = true;
			} else {
				// Ignore this spurious click
			}

			return true;
		case R.id.action_home:
			set_liste(ALLE_FOLGEN);
			return true;
		case R.id.action_check_neue_folgen:
			set_liste(NEUE_FOLGEN);
			return true;
		case R.id.action_check_ungesehen:
			set_liste(NOCH_NICHT_GESEHEN_FOLGEN);
			return true;
		case R.id.action_add:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			dialog.setView(input);
			dialog.setMessage(Messages.getString("MainActivity.2")) //$NON-NLS-1$
					.setPositiveButton(
							Messages.getString("MainActivity.3"), new DialogInterface.OnClickListener() { //$NON-NLS-1$
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									String inhalt = input.getText().toString();
									if (Datenbank.check_serie_vorhanden(inhalt)) {
										Datenbank.hinzufuegen_serie(inhalt);
										set_liste(NOCH_NICHT_GESEHEN_FOLGEN);
									}
								}
							})
					.setNegativeButton(
							Messages.getString("MainActivity.4"), new DialogInterface.OnClickListener() { //$NON-NLS-1$
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			dialog.show();
			return true;
		case R.id.action_delete:
			Spinner SpinnerSerie = (Spinner) findViewById(R.id.sTage);
			String text = SpinnerSerie.getItemAtPosition(
					SpinnerSerie.getLastVisiblePosition()).toString();
			Datenbank.loeschen_serie(text);
			set_liste(NOCH_NICHT_GESEHEN_FOLGEN);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@SuppressLint({ "NewApi", "InlinedApi" })
	public static class Serien_ListFragment extends Fragment {

		public Serien_ListFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			final View rootView = inflater.inflate(R.layout.fragment_serielist,
					container, false);
			final Context con = container.getContext();
			TextView kopf = (TextView) rootView.findViewById(R.id.Head);
			kopf.setText(head);
			final ArrayList<String> ListeSerie = spinnerserie;
			ArrayAdapter<String> AdapterSerie = new ArrayAdapter<String>(con,
					R.layout.spinner_string, ListeSerie);
			Spinner SpinnerSerie = (Spinner) rootView.findViewById(R.id.sTage);
			SpinnerSerie.setAdapter(AdapterSerie);
			SpinnerSerie
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

							final String LSName = ListeSerie.get(position);
							spinnerstaffeln = Datenbank.ausgabe_staffeln(head,
									Datenbank.konvert_serienname_to_id(LSName),
									days);
							final ArrayList<String> ListeStaffel = spinnerstaffeln;
							ArrayAdapter<String> AdapterStaffel = new ArrayAdapter<String>(
									con, R.layout.spinner_ints, ListeStaffel);
							Spinner SpinnerStaffel = (Spinner) rootView
									.findViewById(R.id.sStaffel);
							SpinnerStaffel.setAdapter(AdapterStaffel);
							SpinnerStaffel
									.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

										@Override
										public void onItemSelected(
												AdapterView<?> parent,
												View view, int position, long id) {
											final int LEStaffel = Integer
													.parseInt(ListeStaffel
															.get(position));
											spinnerepisoden = Datenbank
													.ausgabe_episoden(
															head,
															Datenbank
																	.konvert_serienname_to_id(LSName),
															LEStaffel, days);
											final ArrayList<String> ListeEpisode = spinnerepisoden;
											ArrayAdapter<String> AdapterEpisode = new ArrayAdapter<String>(
													con, R.layout.spinner_ints,
													ListeEpisode);
											Spinner SpinnerEpisode = (Spinner) rootView
													.findViewById(R.id.sFenster);
											SpinnerEpisode
													.setAdapter(AdapterEpisode);
											SpinnerEpisode
													.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

														@Override
														public void onItemSelected(
																AdapterView<?> parent,
																View view,
																int position,
																long id) {
															final int LEEpisode = Integer
																	.parseInt(ListeEpisode
																			.get(position));
															CheckBox CBVorhanden = (CheckBox) rootView
																	.findViewById(R.id.cBVorhanden);
															final CheckBox CBGesehen = (CheckBox) rootView
																	.findViewById(R.id.cBGesehen);
															Button BLink = (Button) rootView
																	.findViewById(R.id.bLink);
															CBVorhanden
																	.setChecked(Datenbank
																			.ausgabe_vorhanden(
																					LSName,
																					LEStaffel,
																					LEEpisode));
															CBGesehen
																	.setChecked(Datenbank
																			.ausgabe_gesehen(
																					LSName,
																					LEStaffel,
																					LEEpisode));
															BLink.setOnClickListener(new OnClickListener() {

																@Override
																public void onClick(
																		View v) {
																	String link = Datenbank
																			.ausgabe_link(
																					LSName,
																					LEStaffel,
																					LEEpisode);
																	Uri uriUrl = Uri
																			.parse(link);
																	new Thread(
																			new Client(Streams_lib.HOST,
																					Streams_lib.OPEN_LINK,
																					link))
																			.start();
																	Intent launchBrowser = new Intent(
																			Intent.ACTION_VIEW,
																			uriUrl);
																	startActivity(launchBrowser);

																}

															});

															CBGesehen
																	.setOnClickListener(new OnClickListener() {

																		@Override
																		public void onClick(
																				View v) {
																			Datenbank
																					.aendern_gesehen_status(
																							Datenbank
																									.konvert_serienname_to_id(LSName),
																							LEStaffel,
																							LEEpisode,
																							CBGesehen
																									.isChecked());
																			// set_liste(head);
																		}

																	});

														}

														@Override
														public void onNothingSelected(
																AdapterView<?> parent) {
															// TODO
															// Auto-generated
															// method stub

														}

													});

										}

										@Override
										public void onNothingSelected(
												AdapterView<?> parent) {
											// TODO Auto-generated method stub

										}

									});
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub

						}

					});

			return rootView;
		}

	}

	// Add app running notification
	@SuppressLint({ "NewApi", "InlinedApi" })
	public static class EinstellungFragment extends Fragment {

		public EinstellungFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			final View rootView = inflater.inflate(R.layout.fragment_setup,
					container, false);
			final Context con = container.getContext();
			TextView kopf = (TextView) rootView.findViewById(R.id.Head);
			kopf.setText(head);

			final ArrayList<String> ListeTage = Streams_lib.TAGELISTE;
			ArrayAdapter<String> AdapterTage = new ArrayAdapter<String>(con,
					R.layout.spinner_string, ListeTage);
			Spinner SpinnerTage = (Spinner) rootView.findViewById(R.id.sTage);
			SpinnerTage.setAdapter(AdapterTage);
			String positiontage = setup
					.get_Parameter(Streams_lib.PARAMETER_NEU_TAGE);
			SpinnerTage.setSelection(ListeTage.indexOf(positiontage));

			NumberPicker nPTage = (NumberPicker) rootView
					.findViewById(R.id.nPTage);
			NumberPicker nPStunden = (NumberPicker) rootView
					.findViewById(R.id.nPStunden);
			NumberPicker nPMinuten = (NumberPicker) rootView
					.findViewById(R.id.nPMinuten);

			String zeit = setup
					.get_Parameter(Streams_lib.PARAMETER_UPDATE_ZEIT);
			int tage = Integer.parseInt(zeit.substring(0, 2));
			int stunden = Integer.parseInt(zeit.substring(3, 5));
			int minuten = Integer.parseInt(zeit.substring(6, 8));

			nPTage.setMaxValue(30);
			nPTage.setMinValue(0);
			nPTage.setWrapSelectorWheel(true);
			nPTage.setValue(tage);
			nPStunden.setMaxValue(23);
			nPStunden.setMinValue(0);
			nPStunden.setWrapSelectorWheel(true);
			nPStunden.setValue(stunden);
			nPMinuten.setMaxValue(59);
			nPMinuten.setMinValue(0);
			nPMinuten.setWrapSelectorWheel(true);
			nPMinuten.setValue(minuten);

			final ArrayList<String> ListeFenster = Streams_lib.FENSTERLISTE;
			ArrayAdapter<String> AdapterFenster = new ArrayAdapter<String>(con,
					R.layout.spinner_string, ListeFenster);
			Spinner SpinnerFenster = (Spinner) rootView
					.findViewById(R.id.sFenster);
			SpinnerFenster.setAdapter(AdapterFenster);
			String position = setup
					.get_Parameter(Streams_lib.PARAMETER_WINDOW);
			SpinnerFenster.setSelection(ListeFenster.indexOf(position));

			Button OkButton = (Button) rootView.findViewById(R.id.bOk);
			OkButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Spinner SpinnerTage = (Spinner) rootView
							.findViewById(R.id.sTage);
					NumberPicker nPTage = (NumberPicker) rootView
							.findViewById(R.id.nPTage);
					NumberPicker nPStunden = (NumberPicker) rootView
							.findViewById(R.id.nPStunden);
					NumberPicker nPMinuten = (NumberPicker) rootView
							.findViewById(R.id.nPMinuten);
					Spinner SpinnerFenster = (Spinner) rootView
							.findViewById(R.id.sFenster);

					String Tage = (String) SpinnerTage.getSelectedItem();
					String Fenster = (String) SpinnerFenster.getSelectedItem();
					int Tagefreq = nPTage.getValue();
					int Stundenfreq = nPStunden.getValue();
					int Minutenfreq = nPMinuten.getValue();
					String updatezeit = pad(String.valueOf(Tagefreq), 2) + ":"
							+ pad(String.valueOf(Stundenfreq), 2) + ":"
							+ pad(String.valueOf(Minutenfreq), 2);
					if (updatezeit.equals("00:00:00")) {
						updatezeit = "00:00:01";
					}
					setup.set_Parameter(
							Streams_lib.PARAMETER_NEU_TAGE, Tage);
					days = Integer.parseInt(Tage);
					setup.set_Parameter(Streams_lib.PARAMETER_WINDOW,
							Fenster);
					setup.set_Parameter(
							Streams_lib.PARAMETER_UPDATE_ZEIT,
							updatezeit);
					Alarm alarm = new Alarm(con);
					alarm.Update(updatezeit);

				}
			});
			return rootView;
		}

	}

	private static String pad(String s, int length) {
		while (s.length() < length)
			s = "0" + s;
		return s;
	}

}
