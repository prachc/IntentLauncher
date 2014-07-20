package com.prach.mashup.ilauncher;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.prach.mashup.waextractor.IWAEService;
import com.prach.mashup.waextractor.IWAEServiceCallback;
import com.prach.mashup.wsconnector.IWSCService;

public class IntentLauncher extends Activity {
	private static final String TAG = "IntentLauncher";
	private Button barcode, gps, finish, wae, waea,waep,camera, wsca, wscp1, wscp2,unbind,gpss,bdba,bdbs;
	private final int WSC_INTENT = 0x005;
	private final int CAM_INTENT = 0x004;
	private final int WAE_INTENT = 0x003;
	private final int BARCODE_INTENT = 0x002;
	private final int GPS_INTENT = 0x001;
	private IWSCService wscservice;
	private IWAEService waeservice;
	private Context ILContext;
	//private Parcel data1,data2;
	private Parcel replywsc1,replywsc2,replywae1,replygps,replybdba,replybdbs;
	private boolean isboundWSC = false,isboundWAE = false;
	
	private ServiceConnection wscconnection = new ServiceConnection(){
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			wscservice = IWSCService.Stub.asInterface(boundService);
			Log.d(TAG, "wscconnection.onServiceConnected() connected");
			Toast.makeText(IntentLauncher.this, "WSCService connected",
					Toast.LENGTH_LONG).show();
			//First click = connect
			//Next click = execute
			//Next click = connect + execute (after unbind)
		}

		public void onServiceDisconnected(ComponentName name) {
			wscservice = null;
			Log.d(TAG, "wscconnection.onServiceDisconnected() disconnected");
			Toast.makeText(IntentLauncher.this, "WSCService disconnected",
					Toast.LENGTH_LONG).show();
		}
	};
	
	private ServiceConnection waeconnection = new ServiceConnection(){
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			waeservice = IWAEService.Stub.asInterface(boundService);
			Log.d(TAG, "waeconnection.onServiceConnected() connected");
			Toast.makeText(IntentLauncher.this, "WAEService connected",
					Toast.LENGTH_LONG).show();
			//First click = connect
			//Next click = execute
			//Next click = connect + execute (after unbind)
		}

		public void onServiceDisconnected(ComponentName name) {
			waeservice = null;
			Log.d(TAG, "waeconnection.onServiceDisconnected() disconnected");
			Toast.makeText(IntentLauncher.this, "WAEService disconnected",
					Toast.LENGTH_LONG).show();
		}
	};
	
	private IWAEServiceCallback waecallback = new IWAEServiceCallback.Stub() {
		public void dofinish() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};

    private static final int SERVICE_WSC_AIDL_READY = 0x001;
    private static final int SERVICE_WAE_AIDL_READY = 0x002;
    private static final int SERVICE_WSC_READY_1 = 0x101;
    private static final int SERVICE_WSC_READY_2 = 0x102;
    private static final int SERVICE_WSC_FAILED = 0x10F;
    private static final int SERVICE_WAE_READY_1 = 0x201;
    private static final int SERVICE_WAE_READY_2 = 0x202;
    private static final int SERVICE_WAE_FAILED = 0x20F;
    private static final int SERVICE_GPS_READY = 0x302;
    private static final int SERVICE_GPS_FAILED = 0x30F;
    private static final int SERVICE_BDBA_READY = 0x401;
    private static final int SERVICE_BDBA_FAILED = 0x40F;
    private static final int SERVICE_BDBS_READY = 0x501;
    private static final int SERVICE_BDBS_FAILED = 0x50F;
    //private static final int SERVICE_PARCEL_FAILED_2 = 0x202;
    private static final int SERVICE_WAE_AIDL_FINISHED = 0xF02;
    
    private Handler fHandler = new Handler(){
    	@Override public void handleMessage(Message msg){
    		switch (msg.what) {
			case SERVICE_WAE_AIDL_FINISHED:
				try {
					String[] outputs = waeservice.getOutputs();
					String[] names = waeservice.getNames();
					
					showDialog("WAE AIDL Data", outputs[0] + "," + names[0]);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;

			default:
				break;
			}
    	}
    };
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVICE_WSC_AIDL_READY:
                	try {
    					String base = "http://ajax.googleapis.com/";
    					String[] paths = { "ajax", "services", "language","translate" };
    					String[] keys = { "q" , "v", "langpair"};
    					String[] values = { "where's the bathroom","1.0","en|ja" };
    					String type = "JSON";
    					String query = "responseData.translatedText";
    					String[] outputs = wscservice.connectWS(type,query,base,paths,keys,values);
    					
    					StringBuffer temp = new StringBuffer();
    					for (int i = 0; i < outputs.length; i++) {
    						temp.append("["+i+"]");
    						temp.append(outputs[i]);
    						temp.append("\n");
    					}
    					showDialog("WSC AIDL Data", temp.toString());
    					
    				}catch (RemoteException e) {
    					Log.d(TAG, "wscp1.onClick failed with: " + e);
    					e.printStackTrace();
    				}
                	
                    break;
                case SERVICE_WAE_AIDL_READY:
                	try {
                		String URL = "http://www.amazon.co.jp/?ie=UTF8&force-full-site=1";
						String[] scripts = new String[2];
						scripts[0] = 
							"prach = new Object;\n"+
							"prach.input = '9780470344712';\n"+
							"var tagArray1 = document.getElementsByTagName('table');\n"+
							"var parentElement;\n"+
							"for(var i=0;i<tagArray1.length;i++){\n"+
							"    if(i>4&&i<12&&tagArray1[i].id=='subDropdownTable'){\n"+
							"        parentElement = tagArray1[i];\n"+
							"        break;\n"+
							"    }\n"+
							"}\n"+
							"var tagArray2 = parentElement.getElementsByTagName('input');\n"+
							"var childElement;\n"+
							"for(var i=0;i<tagArray2.length;i++)\n"+
							"    if(i==0&&tagArray2[i].id=='twotabsearchtextbox'&&tagArray2[i].name=='field-keywords'&&tagArray2[i].className=='searchSelect')\n"+
							"        childElement = tagArray2[i];\n"+
							"childElement.focus();\n"+
							"childElement.value=prach.input;\n"+
							"childElement.form.submit();";
						scripts[1] = 
							"var tagArray1 = document.getElementsByTagName('div');\n"+
							"var parentElement;\n"+
							"for(var i=0;i<tagArray1.length;i++){\n"+
							"    if(i>38&&i<46&&tagArray1[i].className=='title'){\n"+
							"        parentElement = tagArray1[i];\n"+
							"        break;\n"+
							"    }\n"+
							"}\n"+
							"var tagArray2 = parentElement.getElementsByTagName('a');\n"+
							"var childElement;\n"+
							"for(var i=0;i<tagArray2.length;i++)\n"+
							"    if(i==0&&tagArray2[i].className=='title')\n"+
							"        childElement = tagArray2[i];\n"+
							"var ProductTitle = childElement.textContent;"+
							"window.prach.addOutput(ProductTitle,'ProductTitle');" +
							
						    "var ProductPrice = new Array();"+
						    "var tagArray1 = document.getElementsByTagName('span');"+
						    "var parentElement;"+
						    "for(var i=0;i<tagArray1.length;i++){"+
						    "    if(i>=23&&i<31&&tagArray1[i].className=='subPrice'){"+
						    "        parentElement = tagArray1[i];"+
						    "        break;"+
						    "    }"+
						    "}"+
						    "if(parentElement==undefined)"+
						    "    window.prach.setfinishstate('false');"+
						    "/*case 5: single parent&child, single child-tag*/"+
						    "var tagArray2 = parentElement.getElementsByTagName('span');"+
						    "var childElement;"+
						    "for(var i=0;i<tagArray2.length;i++){"+
						    "    if(i==0&&tagArray2[i].className=='price'){"+
						    "        childElement = tagArray2[i];"+
						    "        ProductPrice.push(childElement.textContent);"+
						    "    }"+
						    "}"+
						    "window.prach.addOutputArray(ProductPrice,'ProductPrice');"+
						    "window.prach.setfinishstate('true');";
						
						Log.d("WAE AIDL Service:","startExtract();");
						waeservice.startExtract(URL, scripts);
    				}catch (RemoteException e) {
    					Log.d(TAG, "wscp1.onClick failed with: " + e);
    					e.printStackTrace();
    				}
                	
                    break;
                case SERVICE_WSC_READY_1:
                	Bundle replybundle1 = replywsc1.readBundle();
                	if(replybundle1==null)
                		Log.i("SERVICE_WSC_READY","null");
                	
                	String[] name = replybundle1.getStringArray("Name");
    				String[] category = replybundle1.getStringArray("Category");
    				String[] lat = replybundle1.getStringArray("Latitude");
    				String[] lng = replybundle1.getStringArray("Longitude");
    				StringBuffer temp = new StringBuffer();
    				for (int i = 0; i < 2; i++) {
    					temp.append("["+i+"]");
    					temp.append(name[i]);
    					temp.append("\n");
    				}
    				for (int i = 0; i < 2; i++) {
    					temp.append("["+i+"]");
    					temp.append(category[i]);
    					temp.append("\n");
    				}
    				for (int i = 0; i < 2; i++) {
    					temp.append("["+i+"]");
    					temp.append(lat[i]);
    					temp.append("\n");
    				}
    				for (int i = 0; i < 2; i++) {
    					temp.append("["+i+"]");
    					temp.append(lng[i]);
    					temp.append("\n");
    				}
    				
                	/*String[] outputs1 = replybundle1.getStringArray("OUTPUTS");
					
					StringBuffer temp1 = new StringBuffer();
					for (int i = 0; i < outputs1.length; i++) {
						temp1.append("["+i+"]");
						temp1.append(outputs1[i]);
						temp1.append("\n");
					}*/
					showDialog("WSC Parcel Data 1", temp.toString());
                	break;
                case SERVICE_WSC_READY_2:
                	Bundle replybundle2 = replywsc2.readBundle();
                	if(replybundle2==null)
                		Log.i("SERVICE_WSC_READY","null");
                	String[] price = replybundle2.getStringArray("Price");
					
					StringBuffer temp2 = new StringBuffer();
					for (int i = 0; i < price.length; i++) {
						temp2.append("["+i+"]");
						temp2.append(price[i]);
						temp2.append("\n");
					}
					showDialog("WSC Parcel Data 2", temp2.toString());
                	break;
                case SERVICE_WSC_FAILED:
                	showDialog("WSC Parcel Data", "Service Parcel Failed");
                	break;
                case SERVICE_WAE_READY_1:
                	Bundle replybundle3 = replywae1.readBundle();
                	if(replybundle3==null)
                		Log.i("SERVICE_WSC_READY","null");
                	String[] outputs3 = replybundle3.getStringArray("OUTPUTS");
					
					StringBuffer temp3 = new StringBuffer();
					for (int i = 0; i < outputs3.length; i++) {
						temp3.append("["+i+"]");
						temp3.append(outputs3[i]);
						temp3.append("\n");
					}
					showDialog("WAE Data 1", temp3.toString());
                	break;
                case SERVICE_WAE_FAILED:
                	showDialog("WAE Data", "Service Failed");
                	break;
                case SERVICE_GPS_READY:
                	if(replygps==null)
                		Log.i(TAG,"replygps==null");
                	Bundle replygps1 = replygps.readBundle();
                	if(replygps1==null)
                		Log.i("SERVICE_GPS_READY","null");
                	//String[] coor = replygps1.getStringArray("COOR");
                	String latitude = replygps1.getString("LATITUDE");
    				String longitude = replygps1.getString("LONGITUDE");
    				
					StringBuffer temp4 = new StringBuffer();
					/*for (int i = 0; i < coor.length; i++) {
						temp4.append("["+i+"]");
						temp4.append(coor[i]);
						temp4.append("\n");
					}*/
					temp4.append("[lat] "+latitude);
					temp4.append("[lng] "+longitude);
    				
					showDialog("GPS Parcel Data 2", temp4.toString());
                	break;
                case SERVICE_GPS_FAILED:
                	showDialog("GPS Parcel Data", "Service Parcel Failed");
                	break;
                case SERVICE_BDBA_READY:
                	if(replybdba==null)
                		Log.i(TAG,"replybdba==null");
                	Bundle replybdba1 = replybdba.readBundle();
                	if(replybdba1==null)
                		Log.i("SERVICE_BDBA_READY","null");
                	String statusa = replybdba1.getString(("STATUS"));
					
					showDialog("BDBA Parcel Data", statusa);
                	break;
                case SERVICE_BDBA_FAILED:
                	showDialog("BDBA Parcel Data", "Service Parcel Failed");
                	break;
                case SERVICE_BDBS_READY:
                	if(replybdbs==null)
                		Log.i(TAG,"replybdbs==null");
                	Bundle replybdbs1 = replybdbs.readBundle();
                	if(replybdbs1==null)
                		Log.i("SERVICE_BDBS_READY","null");
                	
                	String[] prices = replybdbs1.getStringArray("PRICE");
                	//String total = ARTotalCalculator(prices);
                	
                	double toutput = 0;
                	for (int i = 0; i < prices.length; i++)
            			toutput += Double.parseDouble(prices[i]);
                	
                	replybdbs1.putString("TOTAL", Double.toString(toutput));
                	JsonBuilder jb = new JsonBuilder();
                	jb.setBundle(replybdbs1);
                	jb.setXML(getXML2());
                	
                	String[] titles = replybdbs1.getStringArray("TITLE");
                	System.out.println("TITLE0:"+titles[0]);
                	//String statuss = replybdbs1.getString(("STATUS"));
                	//String total = replybdbs1.getString(("TOTAL"));
                	
                	
					
					//showDialog("BDBS Parcel Data", "status:"+statuss+"\ntotal:"+total);
                	showDialog("BDBS Json", jb.getJSON());
                	break;
                case SERVICE_BDBS_FAILED:
                	showDialog("BDBS Parcel Data", "Service Parcel Failed");
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    
    public String getXML(){
    	return 
    	"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
    	"<object>\n"+
    	"	<name>ResultStatus</name>\n"+
    	"	<value>Succeed</value>\n"+
    	"	<name>Data</name>\n"+
    	"	<array>\n"+
    	"		<loop>\n"+
    	"			<object>\n"+
    	"				<name>title</name>\n"+
    	"				<value>BookDatabase.output.TITLE</value>\n"+
    	"				<name>isbn</name>\n"+
    	"				<value>BookDatabase.output.ISBN</value>\n"+
    	"				<name>price</name>\n"+
    	"				<value>BookDatabase.output.PRICE</value>\n"+
    	"			</object>\n"+
    	"		</loop>\n"+
    	"	</array>\n"+
    	"	<name>total</name>\n"+
    	"	<value>BookDatabase.output.TOTAL</value>\n"+
    	"</object>";
    }
    
    public String getXML2(){
    	return 
    	"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
		"	<object>\n"+
		"		<name>ResultStatus</name>\n"+
		"		<value>Succeed</value>\n"+
		"		<name>title</name>\n"+
		"		<array>\n"+
		"			<loop>\n"+
		"				<value>BookDatabase.output.TITLE</value>\n"+
		"			</loop>\n"+
		"		</array>\n"+
		"		<name>isbn</name>\n"+
		"		<array>\n"+
		"			<loop>\n"+
		"				<value>BookDatabase.output.ISBN</value>\n"+
		"			</loop>\n"+
		"		</array>\n"+
		"		<name>price</name>\n"+
		"		<array>\n"+
		"			<loop>\n"+
		"				<value>BookDatabase.output.PRICE</value>\n"+
		"			</loop>\n"+
		"		</array>\n"+
		"		<name>total</name>\n"+
		"		<value>BookDatabase.output.TOTAL</value>\n"+
		"	</object>";
    }
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ILContext = (Context)this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//data1 = Parcel.obtain();
		replywsc1 = Parcel.obtain();
		//data2 = Parcel.obtain();
		replywsc2 = Parcel.obtain();
		replywae1 = Parcel.obtain();
		replygps = Parcel.obtain();
		replybdba = Parcel.obtain();
		replybdbs = Parcel.obtain();
		
		barcode = (Button) findViewById(R.id.button_barcode);
		gps = (Button) findViewById(R.id.button_gps);
		gpss = (Button) findViewById(R.id.button_gpss);
		bdbs = (Button) findViewById(R.id.button_bdb_sum);
		bdba = (Button) findViewById(R.id.button_bdb_add);
		wae = (Button) findViewById(R.id.button_wae);
		waea = (Button) findViewById(R.id.button_waea);
		waep = (Button) findViewById(R.id.button_waep);
		camera = (Button) findViewById(R.id.button_cam);
		wsca = (Button) findViewById(R.id.button_wsca);
		wscp1 = (Button) findViewById(R.id.button_wscp1);
		wscp2 = (Button) findViewById(R.id.button_wscp2);
		unbind = (Button) findViewById(R.id.button_unbind);
		finish = (Button) findViewById(R.id.button_finish); 

		barcode.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				startActivityForResult(intent, BARCODE_INTENT);
			}
		});

		gps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0?q=37.423156,-122.084917�@(name)"));
				startActivity(intent);
				/*Intent intent = new Intent("com.prach.mashup.GPS");
				intent.putExtra("MODE", "ACTIVE");
				startActivityForResult(intent, GPS_INTENT);*/
				// showDialog("IntentLauncher","do gps locator");
			}
		});
		
		

		wae.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				/*
				Intent intent = new Intent("com.prach.mashup.WAExtractor");
				StringBuffer extra_HTML_SOURCE = new StringBuffer();
				extra_HTML_SOURCE.append("<html><head><meta http-equiv='content-type' content='text/html;charset=UTF-8'></head>" +
						"<body><div align='center'><big><font style='color:blue'>Mashup Result</font></big><br>");
				extra_HTML_SOURCE.append("<img src='");
				extra_HTML_SOURCE.append("http://photo.goodreads.com/books/1268000033l/3429846.jpg"); 
				extra_HTML_SOURCE.append("'/>");
				extra_HTML_SOURCE.append("<br><big><font style='color:blue'>Price:</font></big>3056 yen<br>");
				extra_HTML_SOURCE.append("<big><font style='color:blue'>Descriptions:</font></big><br></div>");
				//extra_HTML_SOURCE.append("�I�[�v�“�ศ�J”ญ�ย�ซ�๐’๑���ต�AAndroid�อ�A���o�C���f�o�C�X�ฬ�ฝ�฿�ฬ�v�V“I�ศ�A�v���P�[�V���“�๐�‘�ญ�ฝ�฿�ษ�G�L�T�C�e�B�“�O�ศ�V�ต�ข�@�๏�๐•\�ต���ท�B�ฑ�ฬ–{�อ�AAndroid�\�t�g�E�F�A�J”ญ�L�b�g�๐�g—p�ต�ฤ�ฑ�๊�็�ฬ�A�v���P�[�V���“�๐�\’z�ท�้�ฝ�฿�ฬ�ภ‘H“I�ศ�K�C�h�๐’๑���ต���ท�B�ป�๊�อ�A�ป�๊�ผ�๊�ชAndroid�๐�ล‘ๅ�ภ�ษ��—p�ท�้�ฝ�฿�ฬ�V�ต�ข�@”\�ฦ�Z�p�๐“ฑ“��ต�A�T�“�v���ฬ�๊�A�ฬ�v���W�F�N�g�๐�๎�ต�ฤ•\�ฆ�ณ�๊���ท�B� �ศ�ฝ�พ�ฏ�ล�ศ�ญ�A�ศ���ล—L—p�ศ—แ�ฬ�•�ฏ�๐�ุ�่�ฤ�A��“x�ศ�@”\�๐—�—p�ท�้•๛–@�๐”ญ�ฉ�ท�้�ๆ�ค�ษ�ท�ื�ฤ�ฬ�๎–{“I�ศ�@”\�ษ�ย�ข�ฤ�w�ั���ท�B");
				extra_HTML_SOURCE.append("<div align='center'><br><big><font style='color:blue'>Reviews:</font></big><br></div>");
				//extra_HTML_SOURCE.append(TranslatedReview);
				extra_HTML_SOURCE.append("</body></html>");
				intent.putExtra("MODE", "DISPLAY");
				intent.putExtra("HTML_SOURCE", extra_HTML_SOURCE.toString());
				startActivity(intent);
				*/
				
				Intent intent = new Intent("com.prach.mashup.WAExtractor");

				String URL = "http://www.alc.co.jp/";
				String[] scripts = new String[2];
				scripts[0] = "var tagArray1 = document.getElementsByTagName('form');"
						+ "var parentElement;"
						+ "for(var i=0;i<tagArray1.length;i++){"
						+ "    if(i>0&&i<7&&tagArray1[i].id=='fm1'&&tagArray1[i].name=='fm1'){"
						+ "        parentElement = tagArray1[i];"
						+ "        break;"
						+ "    }"
						+ "}"
						+ "var childElements = new Array();"
						+ "var tagArray2 = parentElement.getElementsByTagName('input');"
						+ "for(var i=0;i<tagArray2.length;i++)"
						+ "    if(i==0&&tagArray2[i].name=='q'&&(tagArray2[i].className.indexOf('j12')!=-1&&tagArray2[i].className.indexOf('s_txt')!=-1))"
						+ "        childElements[0] = tagArray2[i];"
						+ "childElements[0].style.backgroundColor = '#FF6699';"
						+ "childElements[0].style.borderColor = '#FF6699';"
						+ "childElements[0].focus();"
						+ "var tagArray2 = parentElement.getElementsByTagName('input');"
						+ "for(var i=0;i<tagArray2.length;i++)"
						+ "    if(i==1&&tagArray2[i].className=='j12')"
						+ "        childElements[1] = tagArray2[i];"
						+ "childElements[1].style.backgroundColor = '#FF6699';"
						+ "childElements[1].style.borderColor = '#FF6699';";

				scripts[1] = "var tagArray1 = document.getElementsByTagName('div');"
						+ "var parentElement;"
						+ "for(var i=0;i<tagArray1.length;i++){"
						+ "    if(i>31&&i<39&&tagArray1[i].id=='resultsList'&&tagArray1[i].className=='mr_10'){"
						+ "        parentElement = tagArray1[i];"
						+ "        break;"
						+ "    }"
						+ "}"
						+ "var tagArray2 = parentElement.getElementsByTagName('ul');"
						+ "var childElement;"
						+ "for(var i=0;i<tagArray2.length;i++)"
						+ "    if(i==1&&tagArray2[i].className=='ul_je')"
						+ "        childElement = tagArray2[i];"
						+ "var WordList = childElement.innerHTML;"
						+ "window.prach.addOutput(WordList,'WordList');";
						//+ "window.prach.setfinishstate('true');";

				intent.putExtra("MODE", "EXTRACTION");
				intent.putExtra("URL", URL);
				intent.putExtra("SCRIPTS", scripts);
				//intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				startActivityForResult(intent, WAE_INTENT);
			}
		});
		
		waea.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Thread serviceThread = new Thread(new Runnable() {
						public void run() {
							Intent intent = new Intent("com.prach.mashup.WAEServiceAIDL");
							isboundWAE = bindService(intent,waeconnection ,Context.BIND_AUTO_CREATE);
							if (waeservice != null&&isboundWAE)
								mHandler.sendEmptyMessage(SERVICE_WAE_AIDL_READY);
						}
					});
					serviceThread.start();
				} catch (Exception ex) {
					Log.e(getClass().getSimpleName(),"Could not complete service call: "+ ex.getLocalizedMessage(), ex);
				}
			}
		});
		
		camera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent imageCaptureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				File out = new File(Environment.getExternalStorageDirectory(),
						"intent_image.jpg");
				Uri uri = Uri.fromFile(out);
				imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				Log.i("IntentLauncher", uri.toString());
				imageCaptureIntent
						.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, "1");
				startActivityForResult(imageCaptureIntent, CAM_INTENT);
			}
		});

		waep.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//Intent intent = new Intent("com.prach.mashup.WAExtractor");
				try {
					Thread serviceThread1 = new Thread(new Runnable() {
						public void run() {
							Intent i = new Intent("com.prach.mashup.WAEService");
							//i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							boolean isConnected = bindService(i,new ServiceConnection(){
								final int serviceCode = 0x101;
								public void onServiceConnected(ComponentName name,IBinder service) {
									Log.d("onServiceConnected","Service connected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service connected",Toast.LENGTH_SHORT);
									Parcel data = Parcel.obtain();
									Bundle bundle = new Bundle();
									
									String URL = "http://www.amazon.co.jp/?ie=UTF8&force-full-site=1";
									String[] scripts = new String[2];
									scripts[0] = 
										"prach = new Object;\n"+
										"prach.input = '9780470344712';\n"+
										"var tagArray1 = document.getElementsByTagName('table');\n"+
										"var parentElement;\n"+
										"for(var i=0;i<tagArray1.length;i++){\n"+
										"    if(i>4&&i<12&&tagArray1[i].id=='subDropdownTable'){\n"+
										"        parentElement = tagArray1[i];\n"+
										"        break;\n"+
										"    }\n"+
										"}\n"+
										"var tagArray2 = parentElement.getElementsByTagName('input');\n"+
										"var childElement;\n"+
										"for(var i=0;i<tagArray2.length;i++)\n"+
										"    if(i==0&&tagArray2[i].id=='twotabsearchtextbox'&&tagArray2[i].name=='field-keywords'&&tagArray2[i].className=='searchSelect')\n"+
										"        childElement = tagArray2[i];\n"+
										"childElement.focus();\n"+
										"childElement.value=prach.input;\n"+
										"childElement.form.submit();";
									scripts[1] = 
										"var tagArray1 = document.getElementsByTagName('div');\n"+
										"var parentElement;\n"+
										"for(var i=0;i<tagArray1.length;i++){\n"+
										"    if(i>38&&i<46&&tagArray1[i].className=='title'){\n"+
										"        parentElement = tagArray1[i];\n"+
										"        break;\n"+
										"    }\n"+
										"}\n"+
										"var tagArray2 = parentElement.getElementsByTagName('a');\n"+
										"var childElement;\n"+
										"for(var i=0;i<tagArray2.length;i++)\n"+
										"    if(i==0&&tagArray2[i].className=='title')\n"+
										"        childElement = tagArray2[i];\n"+
										"var ProductTitle = childElement.textContent;"+
										"window.prach.addOutput(ProductTitle,'ProductTitle');" +
										
									    "var ProductPrice = new Array();"+
									    "var tagArray1 = document.getElementsByTagName('span');"+
									    "var parentElement;"+
									    "for(var i=0;i<tagArray1.length;i++){"+
									    "    if(i>=23&&i<31&&tagArray1[i].className=='subPrice'){"+
									    "        parentElement = tagArray1[i];"+
									    "        break;"+
									    "    }"+
									    "}"+
									    "if(parentElement==undefined)"+
									    "    window.prach.setfinishstate('false');"+
									    "/*case 5: single parent&child, single child-tag*/"+
									    "var tagArray2 = parentElement.getElementsByTagName('span');"+
									    "var childElement;"+
									    "for(var i=0;i<tagArray2.length;i++){"+
									    "    if(i==0&&tagArray2[i].className=='price'){"+
									    "        childElement = tagArray2[i];"+
									    "        ProductPrice.push(childElement.textContent);"+
									    "    }"+
									    "}"+
									    "window.prach.addOutputArray(ProductPrice,'ProductPrice');"+
									    "window.prach.setfinishstate('true');";
									
									/*String base = "http://ajax.googleapis.com/";
									String[] paths = { "ajax", "services", "language","translate" };
									String[] keys = { "q" , "v", "langpair"};
									String[] values = { "where's the bathroom","1.0","en|ja" };
									String mode = "JSON";
									String query = "responseData.translatedText";*/

									bundle.putString("URL", URL);
									bundle.putStringArray("SCRIPTS",scripts);
									bundle.putString("MODE", "EXTRACTION");
									/*bundle.putStringArray("KEYS", keys);
									bundle.putStringArray("VALUES",values);
									bundle.putString("QUERY", query);
									bundle.putString("MODE", mode);*/
									//bundle.putString("OUTPUT_NAME", "OUTPUTS");

									data.writeBundle(bundle);
									boolean res = false;
									try {
										res = service.transact(serviceCode, data,replywae1, 0);
									} catch (RemoteException ex) {
										Log.e("onServiceConnected","Remote exception when calling service",ex);
										res = false;
									}
									if (res)
										mHandler.sendEmptyMessage(SERVICE_WAE_READY_1);
									else
										mHandler.sendEmptyMessage(SERVICE_WAE_FAILED);
								}

								public void onServiceDisconnected(ComponentName name) {
									Log.d("onServiceConnected","Service disconnected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service disconnected",Toast.LENGTH_SHORT);								
								}
							}, Context.BIND_AUTO_CREATE);
							
							if (!isConnected) {
								Log.d("bkgd runnable 1","Service could not be connected ");
								mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
							}
						}
					});
					serviceThread1.start();
				
					
				} catch (Exception ex) {
					Log.e(getClass().getSimpleName(),
							"Could not complete service call: "
									+ ex.getLocalizedMessage(), ex);
				}
				//intent.putExtra("URL", URL);
				//intent.putExtra("SCRIPTS", scripts);
				//intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				//startActivityForResult(intent, WAE_INTENT);
			}
		});
		
		wsca.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.prach.mashup.WSConnector");

				String base = "http://ajax.googleapis.com/";
				String[] paths = { "ajax", "services", "language","translate" };
				String[] keys = { "q" , "v", "langpair"};
				String[] values = { "where's the bathroom","1.0","en|ja" };
				String format = "JSON";
				
				String[] name = {"Text","Detail","Status"};
				String[] type = {"single","single","single"};
				String[] query = {"responseData.translatedText","responseDetails","responseStatus"};
				String[] index = {"null","null","null"};
				
				intent.putExtra("BASE", base); 
				intent.putExtra("PATHS", paths);
				intent.putExtra("KEYS", keys);
				intent.putExtra("VALUES", values);
				intent.putExtra("FORMAT", format);
				
				intent.putExtra("NAME", name);
				intent.putExtra("TYPE", type);
				intent.putExtra("QUERY", query);
				intent.putExtra("INDEX", index);
				
				startActivityForResult(intent, WSC_INTENT);
			}
		});

		wscp1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Thread serviceThread = new Thread(new Runnable() {
						public void run() {
							Intent intent = new Intent("com.prach.mashup.WSCServiceAIDL");
							isboundWSC = bindService(intent, wscconnection,Context.BIND_AUTO_CREATE);
							if (wscservice != null&&isboundWSC)
								mHandler.sendEmptyMessage(SERVICE_WSC_AIDL_READY);
						}
					});
					serviceThread.start();
				} catch (Exception ex) {
					Log.e(getClass().getSimpleName(),"Could not complete service call: "+ ex.getLocalizedMessage(), ex);
				}
			}
		});
		
		wscp2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Thread serviceThread1 = new Thread(new Runnable() {
						public void run(){
							Intent i = new Intent("com.prach.mashup.WSCService");
							//i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							boolean isConnected = bindService(i,new ServiceConnection(){
								final int serviceCode = 0x101;
								public void onServiceConnected(ComponentName cname,IBinder service) {
									Log.d("onServiceConnected","Service connected: "+ 
											cname.flattenToShortString());
									Toast.makeText(ILContext,"Service connected",Toast.LENGTH_SHORT);
									Parcel data = Parcel.obtain();
									Bundle bundle = new Bundle();

									String base = "http://api.gnavi.co.jp/";
									String[] paths = { "ver1", "RestSearchAPI", "null"};
									String[] keys = { "keyid" , "input_coordinates_mode", "coordinates_mode", "latitude", "longitude", "hit_per_page", "range"};
									String[] values = { "10d9098dba2f680c748de5b03b28940d","2","2","35.610435337706654","139.68769133090973","8","4" };
									String format = "XML";
									
									String[] name = {"Name","Category","Latitude","Longitude"};
									String[] type = {"multiple","multiple","multiple","multiple"};
									String[] query = {"//name","//category","//latitude","//longitude"};
									String[] index = {"null","null","null","null"};

									bundle.putString("BASE", base); 
									bundle.putStringArray("PATHS", paths);
									bundle.putStringArray("KEYS", keys);
									bundle.putStringArray("VALUES", values);
									bundle.putString("FORMAT", format);
									Log.i("format",format);
									
									bundle.putStringArray("NAME", name);
									bundle.putStringArray("TYPE", type);
									bundle.putStringArray("QUERY", query);
									bundle.putStringArray("INDEX", index);
									//bundle.putStringArray("OUTPUT_NAME", "OUTPUTS");

									data.writeBundle(bundle);
									boolean res = false;
									try {
										res = service.transact(serviceCode, data,replywsc1, 0);
									} catch (RemoteException ex) {
										Log.e("onServiceConnected",
												"Remote exception when calling service",ex);
										res = false;
									}
									if (res)
										mHandler.sendEmptyMessage(SERVICE_WSC_READY_1);
									else
										mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
								}

								public void onServiceDisconnected(ComponentName name) {
									Log.d("onServiceConnected","Service disconnected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service disconnected",Toast.LENGTH_SHORT);								
								}
							}, Context.BIND_AUTO_CREATE);
							
							if (!isConnected) {
								Log.d("bkgd runnable 1","Service could not be connected ");
								mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
							}
						}
						
						/*public void run() {
							Intent i = new Intent("com.prach.mashup.WSCService");
							//i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							boolean isConnected = bindService(i,new ServiceConnection(){
								final int serviceCode = 0x101;
								public void onServiceConnected(ComponentName cname,IBinder service) {
									Log.d("onServiceConnected","Service connected: "+ 
											cname.flattenToShortString());
									Toast.makeText(ILContext,"Service connected",Toast.LENGTH_SHORT);
									Parcel data = Parcel.obtain();
									Bundle bundle = new Bundle();

									String base = "http://ajax.googleapis.com/";
									String[] paths = { "ajax", "services", "language","translate" };
									String[] keys = { "q" , "v", "langpair"};
									String[] values = { "where's the bathroom","1.0","en|ja" };
									String format = "JSON";
									
									String[] name = {"Text","Detail","Status"};
									String[] type = {"single","single","single"};
									String[] query = {"responseData.translatedText","responseDetails","responseStatus"};
									String[] index = {"null","null","null"};

									bundle.putString("BASE", base); 
									bundle.putStringArray("PATHS", paths);
									bundle.putStringArray("KEYS", keys);
									bundle.putStringArray("VALUES", values);
									bundle.putString("FORMAT", format);
									Log.i("format",format);
									
									bundle.putStringArray("NAME", name);
									bundle.putStringArray("TYPE", type);
									bundle.putStringArray("QUERY", query);
									bundle.putStringArray("INDEX", index);
									//bundle.putStringArray("OUTPUT_NAME", "OUTPUTS");

									data.writeBundle(bundle);
									boolean res = false;
									try {
										res = service.transact(serviceCode, data,replywsc1, 0);
									} catch (RemoteException ex) {
										Log.e("onServiceConnected",
												"Remote exception when calling service",ex);
										res = false;
									}
									if (res)
										mHandler.sendEmptyMessage(SERVICE_WSC_READY_1);
									else
										mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
								}

								public void onServiceDisconnected(ComponentName name) {
									Log.d("onServiceConnected","Service disconnected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service disconnected",Toast.LENGTH_SHORT);								
								}
							}, Context.BIND_AUTO_CREATE);
							
							if (!isConnected) {
								Log.d("bkgd runnable 1","Service could not be connected ");
								mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
							}
						}*/
					});
					
					/*Thread serviceThread2 = new Thread(new Runnable() {
						public void run() {
							Intent i = new Intent("com.prach.mashup.WSCService");
							//i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							boolean isConnected = bindService(i,new ServiceConnection(){
								final int serviceCode = 0x101;
								public void onServiceConnected(ComponentName name,IBinder service) {
									Log.d("onServiceConnected","Service connected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service connected",Toast.LENGTH_SHORT);
									Bundle bundle = new Bundle();
									Parcel data = Parcel.obtain();
									
									String base = "http://api.bing.net/";
									String paths[] = {"json.aspx"};
									String keys[] = {"AppId","Version","Market","Query","Sources","Web.Count","JsonType"};
									String values[] = {"3DD24C7A876D47445428610EDCEC78CF98695754","2.2","en-US","testing","web","3","raw"};
									String query = "SearchResponse.Web.Results[all].Url";
									String mode = "JSON";

									bundle.putString("BASE", base);
									bundle.putStringArray("PATHS",paths);
									bundle.putStringArray("KEYS", keys);
									bundle.putStringArray("VALUES",values);
									bundle.putString("QUERY", query);
									bundle.putString("MODE", mode);
									//bundle.putString("OUTPUT_NAME", "OUTPUTS");

									data.writeBundle(bundle);
									boolean res = false;
									try {
										res = service.transact(serviceCode, data,replywsc2, 0);
									} catch (RemoteException ex) {
										Log.e("onServiceConnected","Remote exception when calling service",ex);
										res = false;
									}
									if (res)
										mHandler.sendEmptyMessage(SERVICE_WSC_READY_2);
									else
										mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
								}

								public void onServiceDisconnected(ComponentName name) {
									Log.d("onServiceConnected","Service disconnected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service disconnected",Toast.LENGTH_SHORT);								
								}
							}, Context.BIND_AUTO_CREATE);
							
							if (!isConnected) {
								Log.d("bkgd runnable 2","Service could not be connected ");
								mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
							}
						}
					});*/
					
					Thread serviceThread2 = new Thread(new Runnable() {
						public void run() {
							Intent i = new Intent("com.prach.mashup.WSCService");
							//i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							boolean isConnected = bindService(i,new ServiceConnection(){
								final int serviceCode = 0x101;
								public void onServiceConnected(ComponentName cname,IBinder service) {
									Log.d("onServiceConnected","ExchangeRate WS Service connected: "+ cname.flattenToShortString());

									Parcel data = Parcel.obtain();
									Bundle bundle = new Bundle();

									String base = "http://www.exchangerate-api.com/";
									String[] paths = { "usd", "jpy", "20" };
									String[] keys = { "k"};
									String[] values = { "pQkn3-quzTZ-PNDav" };
									String format = "SELF";
									Log.i("format",format);
									
									String[] name = {"Price"};
									String[] type = {"single"};
									String[] query = {"null"};
									String[] index = {"null"};

									bundle.putString("BASE", base);
									bundle.putStringArray("PATHS",paths);
									bundle.putStringArray("KEYS", keys);
									bundle.putStringArray("VALUES",values);
									bundle.putString("FORMAT", format);
									
									bundle.putStringArray("NAME", name);
									bundle.putStringArray("TYPE", type);
									bundle.putStringArray("QUERY", query);
									bundle.putStringArray("INDEX", index);
									
									data.writeBundle(bundle);
									boolean res = false;
									try {
										res = service.transact(serviceCode, data,replywsc2, 0);
									} catch (RemoteException ex) {
										Log.e("onServiceConnected",
												"Remote exception when calling service",ex);
										res = false;
									}
									if (res)
										mHandler.sendEmptyMessage(SERVICE_WSC_READY_2);
									else
										mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
								}

								public void onServiceDisconnected(ComponentName name) {
									Log.d("onServiceConnected","Service disconnected: "+ name.flattenToShortString());
													
								}
							}, Context.BIND_AUTO_CREATE);

							if (!isConnected) {
								Log.d("bkgd runnable 1","Service could not be connected ");
								mHandler.sendEmptyMessage(SERVICE_WSC_FAILED);
							}
						}
					});
					
					serviceThread1.start();
					serviceThread2.start();
					
				} catch (Exception ex) {
					Log.e(getClass().getSimpleName(),
							"Could not complete service call: "
									+ ex.getLocalizedMessage(), ex);
				}
			}
		});
		
		gpss.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try { 
					Thread gpsThread = new Thread(new Runnable() {
						public void run() {
							Intent i = new Intent("com.prach.mashup.GPSService");
							//i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							boolean isConnected = bindService(i,new ServiceConnection(){
								final int serviceCode = 0x67707301;
								public void onServiceConnected(ComponentName name,IBinder service) {
									Log.d("onServiceConnected","Service connected: "+ 
											name.flattenToShortString());
									Toast.makeText(ILContext,"Service connected",Toast.LENGTH_SHORT);
									Parcel data = Parcel.obtain();
									Bundle bundle = new Bundle();

									String mode = "PASSIVE";
									String type = "null";
									

									//bundle.putString("MODE", mode);
									bundle.putString("TYPE",type);

									//bundle.putString("OUTPUT_NAME", "OUTPUTS");

									data.writeBundle(bundle);
									boolean res = false;
									try {
										res = service.transact(serviceCode, data,replygps, 0);
									} catch (RemoteException ex) {
										Log.e("onServiceConnected",
												"Remote exception when calling service",ex);
										res = false;
									}
									
									if (res)
										mHandler.sendEmptyMessage(SERVICE_GPS_READY);
									else
										mHandler.sendEmptyMessage(SERVICE_GPS_FAILED);
								}

								public void onServiceDisconnected(ComponentName name) {
									Log.d("onServiceConnected","Service disconnected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service disconnected",Toast.LENGTH_SHORT);								
								}
							}, Context.BIND_AUTO_CREATE);

							if (!isConnected) {
								Log.d("gps service","Service could not be connected ");
								mHandler.sendEmptyMessage(SERVICE_GPS_FAILED);
							}
						}
					});

					Log.d("","starting gpss");
					gpsThread.start();


				} catch (Exception ex) {
					Log.e(getClass().getSimpleName(),
							"Could not complete service call: "
							+ ex.getLocalizedMessage(), ex);
				}
			}

		});
		
		bdba.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try { 
					Thread gpsThread = new Thread(new Runnable() {
						public void run() {
							Intent i = new Intent("com.prach.mashup.BookDatabaseService");
							//i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							boolean isConnected = bindService(i,new ServiceConnection(){
								final int serviceCode = 0x66686601;
								public void onServiceConnected(ComponentName name,IBinder service) {
									Log.d("onServiceConnected","BDBA Service connected: "+ 
											name.flattenToShortString());
									Toast.makeText(ILContext,"BDBA Service connected",Toast.LENGTH_SHORT);
									Parcel data = Parcel.obtain();
									Bundle bundle = new Bundle();
									
									String command = "ADD";
									String title = "Prach's book";
									String isbn = "124567890";
									String price = "500";

									//bundle.putString("MODE", mode);
									bundle.putString("COMMAND",command);
									bundle.putString("TITLE",title);
									bundle.putString("ISBN",isbn);
									bundle.putString("PRICE",price);

									//bundle.putString("OUTPUT_NAME", "OUTPUTS");

									data.writeBundle(bundle);
									boolean res = false;
									try {
										res = service.transact(serviceCode, data,replybdba, 0);
									} catch (RemoteException ex) {
										Log.e("onServiceConnected",
												"Remote exception when calling service",ex);
										res = false;
									}
									
									if (res)
										mHandler.sendEmptyMessage(SERVICE_BDBA_READY);
									else
										mHandler.sendEmptyMessage(SERVICE_BDBA_FAILED);
								}

								public void onServiceDisconnected(ComponentName name) {
									Log.d("onServiceConnected","Service disconnected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service disconnected",Toast.LENGTH_SHORT);								
								}
							}, Context.BIND_AUTO_CREATE);

							if (!isConnected) {
								Log.d("bdba service","Service could not be connected ");
								mHandler.sendEmptyMessage(SERVICE_BDBA_FAILED);
							}
						}
					});

					Log.d("","starting bdba");
					gpsThread.start();


				} catch (Exception ex) {
					Log.e(getClass().getSimpleName(),
							"Could not complete service call: "
							+ ex.getLocalizedMessage(), ex);
				}
			}

		});
		
		bdbs.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try { 
					Thread gpsThread = new Thread(new Runnable() {
						public void run() {
							Intent i = new Intent("com.prach.mashup.BookDatabaseService");
							//i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
							boolean isConnected = bindService(i,new ServiceConnection(){
								final int serviceCode = 0x66686601;
								public void onServiceConnected(ComponentName name,IBinder service) {
									Log.d("onServiceConnected","BDBA Service connected: "+ 
											name.flattenToShortString());
									Toast.makeText(ILContext,"BDBA Service connected",Toast.LENGTH_SHORT);
									Parcel data = Parcel.obtain();
									Bundle bundle = new Bundle();

									String command = "SUM";
									

									//bundle.putString("MODE", mode);
									bundle.putString("COMMAND",command);
									
									//bundle.putString("OUTPUT_NAME", "OUTPUTS");

									data.writeBundle(bundle);
									boolean res = false;
									try {
										res = service.transact(serviceCode, data,replybdbs, 0);
									} catch (RemoteException ex) {
										Log.e("onServiceConnected",
												"Remote exception when calling service",ex);
										res = false;
									}
									
									if (res)
										mHandler.sendEmptyMessage(SERVICE_BDBS_READY);
									else
										mHandler.sendEmptyMessage(SERVICE_BDBS_FAILED);
								}

								public void onServiceDisconnected(ComponentName name) {
									Log.d("onServiceConnected","Service disconnected: "+ name.flattenToShortString());
									Toast.makeText(ILContext,"Service disconnected",Toast.LENGTH_SHORT);								
								}
							}, Context.BIND_AUTO_CREATE);

							if (!isConnected) {
								Log.d("bdbs service","Service could not be connected ");
								mHandler.sendEmptyMessage(SERVICE_BDBS_FAILED);
							}
						}
					});

					Log.d("","starting bdbs");
					gpsThread.start();


				} catch (Exception ex) {
					Log.e(getClass().getSimpleName(),
							"Could not complete service call: "
							+ ex.getLocalizedMessage(), ex);
				}
			}

		});

		unbind.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (wscconnection!=null&&isboundWSC) {
					isboundWSC=false;
					unbindService(wscconnection);
					//connection = null;
					Log.d(TAG, "releaseService() unbound.");
				}
				if (waeconnection!=null&&isboundWAE) {
					isboundWAE=false;
					unbindService(waeconnection);
					//connection = null;
					Log.d(TAG, "releaseService() unbound.");
				}
				String external_lat = "35.607568";
				String external_lng = "139.667577";
				String external_name = "�}�N�h�i���h �ฉ—R�ช�u";
				
				String lat = "35.60490863333334";
				String lng = "139.685582";
				
				//String uri = "http://maps.google.com/maps?saddr="+lat+","+lng+"(Here)&daddr="+external_lat+","+external_lng+"("+external_name+")";  
				String uri = "geo:0,0?q="+external_lat+","+external_lng+" (" + external_name + ")";
				startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
			}
		});
		
		finish.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				IntentLauncher.this.finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		if (wscconnection!=null&&isboundWSC) {
			isboundWSC=false;
			unbindService(wscconnection);
			wscconnection = null;
			Log.d(TAG, "releaseService() unbound.");
		}
		if (waeconnection!=null&&isboundWAE) {
			isboundWAE=false;
			unbindService(waeconnection);
			waeconnection = null;
			Log.d(TAG, "releaseService() unbound.");
		}
		super.onDestroy();
	}

	private void showDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", null);
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == BARCODE_INTENT) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				showDialog("Scan Succeed", "Format: " + format + "\nContents: "
						+ contents);
			} else if (resultCode == RESULT_CANCELED) {
				showDialog("BARCODE_INTENT", "RESULT_CANCELED");
			}
		} else if (requestCode == GPS_INTENT) {
			if (resultCode == RESULT_OK) {
				//String[] coor = intent.getStringArrayExtra("COOR");
				String latitude = intent.getStringExtra("LATITUDE");
				String longitude = intent.getStringExtra("LONGITUDE");
				String provider = intent.getStringExtra("PROVIDER");
				showDialog("GPS Data", "Latitude: " + latitude + "\nLongitude: "
						+ longitude + "\nProvider: " + provider);
			} else if (resultCode == RESULT_CANCELED) {
				showDialog("GPS_INTENT", "RESULT_CANCELED");
			}
		} else if (requestCode == WAE_INTENT) {
			if (resultCode == RESULT_OK) {
				String[] outputs = intent.getStringArrayExtra("OUTPUTS");
				String[] names = intent.getStringArrayExtra("NAMES");
				showDialog("WAE Data", outputs[0] + "," + names[0]);
			} else if (resultCode == RESULT_CANCELED) {
				showDialog("WAE_INTENT", "RESULT_CANCELED");
			}
		} else if (requestCode == CAM_INTENT) {
			if (resultCode == RESULT_OK) {
				showDialog("CAM Data", "Image OK");
			} else if (resultCode == RESULT_CANCELED) {
				showDialog("CAM_INTENT", "RESULT_CANCELED");
			}
		} else if (requestCode == WSC_INTENT) {
			if (resultCode == RESULT_OK) {
				String[] text = intent.getStringArrayExtra("Text");
				String[] detail = intent.getStringArrayExtra("Detail");
				String[] status = intent.getStringArrayExtra("Status");
				StringBuffer temp = new StringBuffer();
				for (int i = 0; i < text.length; i++) {
					temp.append("["+i+"]");
					temp.append(text[i]);
					temp.append("\n");
				}
				for (int i = 0; i < detail.length; i++) {
					temp.append("["+i+"]");
					temp.append(detail[i]);
					temp.append("\n");
				}
				for (int i = 0; i < status.length; i++) {
					temp.append("["+i+"]");
					temp.append(status[i]);
					temp.append("\n");
				}
				showDialog("WSCA Data", temp.toString());
			} else if (resultCode == RESULT_CANCELED) {
				showDialog("WSC_INTENT", "RESULT_CANCELED");
			}
		}
	}
}