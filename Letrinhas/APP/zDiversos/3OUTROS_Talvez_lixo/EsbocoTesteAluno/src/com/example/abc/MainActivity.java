package com.example.abc;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	private Button Iniciar, Terminar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 Iniciar= (Button)findViewById(R.id.btIniciar);
         Terminar = (Button)findViewById(R.id.btTerminar);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	 public void Inicia(View v)
	    { 
		 try{
	        //Iniciar.setEnabled(false);
	        //Terminar.setEnabled(true);
			 Toast.makeText(getApplicationContext(), "Grava��o iniciada!",
		    	      Toast.LENGTH_LONG).show();
		 }
		 catch(IllegalStateException e){ }
		 Toast.makeText(getApplicationContext(), "Erro ao iniciar grava��o!", Toast.LENGTH_LONG).show();
	    }	
	 public void Termina(View v)
	    { 
		 	try{
	        //Iniciar.setClickable(true);
	        //Terminar.setClickable(false);
		      Toast.makeText(getApplicationContext(), "Grava��o conclu�da!",
		    	      Toast.LENGTH_LONG).show();
		 	}
		 	catch(IllegalStateException e){
		 		Toast.makeText(getApplicationContext(), "Erro ao terminar grava��o!",
			    	      Toast.LENGTH_LONG).show();
		 	}
		 	}
		 	
		 	
}
