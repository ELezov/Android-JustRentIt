
package elezov.com.justrentit;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import elezov.com.justrentit.model.Advert;
import elezov.com.justrentit.model.Category;

import static android.app.Activity.RESULT_OK;

/**
 * Created by USER on 10.03.2017.
 */

public class AddAdvertFragment extends Fragment {

    private Button loadButton;
    private ImageView image;
    Button makeAdvertBtn;
    EditText nameAdvert;
    EditText descriptionAdvert;
    EditText pricePerDay;
    EditText pricePerWeek;
    EditText pricePerMonth;
    EditText depositEdit;
    MaskedEditText phoneNumberEdit;

    ImageView logoEncode;

    Spinner category;
    Category selectSpiner;
    CategorySpinnerAdapter adapter;
    Utils utils;
    private MobileServiceClient mClient;
    private static final int REQUEST = 1;

    ProgressDialog progressDialog;

    private nextAfterAddAdvertClickListener callback;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    protected static final int REQUEST_CODE_MANUAL = 5;

    interface nextAfterAddAdvertClickListener{
        void nextAfterAddAdvert();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.add_advert_fragment,container,false);
        image = (ImageView) rootView.findViewById(R.id.advert_imageview);
        loadButton = (Button) rootView.findViewById(R.id.load_photo);
        nameAdvert=(EditText) rootView.findViewById(R.id.name_advert_edit);
        descriptionAdvert=(EditText)rootView.findViewById(R.id.description_advert_edit);
        makeAdvertBtn=(Button)rootView.findViewById(R.id.make_advert);
        pricePerDay=(EditText)rootView.findViewById(R.id.price_edit_per_day);
        pricePerWeek=(EditText)rootView.findViewById(R.id.price_edit_per_week);
        pricePerMonth=(EditText) rootView.findViewById(R.id.price_edit_per_month);
        depositEdit=(EditText)rootView.findViewById(R.id.pledge_edit);
        phoneNumberEdit=(MaskedEditText) rootView.findViewById(R.id.phone_input);
        //phoneNumberEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        category=(Spinner) rootView.findViewById(R.id.category_add_spiner);
        final CheckBox checkBox=(CheckBox) rootView.findViewById(R.id.checkbox);
        final ImageView passport=(ImageView) rootView.findViewById(R.id.passport);



        passport.setVisibility(View.INVISIBLE);
        adapter=new CategorySpinnerAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
        utils=Utils.getInstance();
        adapter.data=utils.getListCategory();
        category.setAdapter(adapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSpiner=adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        callback = (nextAfterAddAdvertClickListener) getActivity();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    passport.setVisibility(View.VISIBLE);
                }
                else {
                    passport.setVisibility(View.INVISIBLE);
                }
            }
        });

        try {
            mClient = new MobileServiceClient(utils.getURL_AZURE(), getContext());
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("MainActivity", e.getCause().getMessage());
        }

        makeAdvertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameAdvert.getText().toString().equals("")
                        || descriptionAdvert.getText().toString().equals("")
                        || pricePerDay.getText().toString().equals("")
                        ||pricePerWeek.getText().toString().equals("")
                        ||pricePerMonth.getText().toString().equals("")
                        ||depositEdit.getText().toString().equals("")
                        ||phoneNumberEdit.getText().toString().equals("+7(000)000-00-00"))
                {
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
                else {

                    Log.v("name",nameAdvert.getText().toString());
                    Log.v("description",descriptionAdvert.getText().toString());
                    Log.v("price1",pricePerDay.getText().toString());
                    Log.v("price2",pricePerWeek.getText().toString());
                    Log.v("price3",pricePerMonth.getText().toString());
                    Log.v("deposit",depositEdit.getText().toString());
                    Log.v("phone",phoneNumberEdit.getText().toString());
                    progressDialog=new ProgressDialog(getContext());
                    progressDialog.setTitle("Loading...");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    Advert advert=new Advert();
                    advert.setName(nameAdvert.getText().toString());
                    advert.setDescription(descriptionAdvert.getText().toString());

                    image.buildDrawingCache();
                    Bitmap bitmap = image.getDrawingCache();
                    ByteArrayOutputStream stream=new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] image=stream.toByteArray();
                    String img_str = Base64.encodeToString(image,0);
                    //byte[] decodedByte = Base64.decode(img_str,0);
                    //logoEncode.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));
                    //Log.v("Count",""+img_str.length());
                    Log.v("string:",img_str);
                    advert.setStringPhoto(img_str);
                    advert.setId_category(selectSpiner.getId());
                    advert.setId_user(utils.getUser_Mail());
                    advert.setPricePerDay(Double.parseDouble(pricePerDay.getText().toString()));
                    advert.setPricePerWeek(Double.parseDouble(pricePerWeek.getText().toString()));
                    advert.setPricePerMonth(Double.parseDouble(pricePerMonth.getText().toString()));
                    advert.setDeposit(Double.parseDouble(depositEdit.getText().toString()));
                    advert.setPhoneNumber(phoneNumberEdit.getText().toString());
                    if (checkBox.isChecked())
                        advert.setDocument(true);
                    mClient.getTable(Advert.class).insert(advert, new TableOperationCallback<Advert>() {
                        @Override
                        public void onCompleted(Advert entity, Exception exception, ServiceFilterResponse response) {
                            if (exception==null)
                            {
                                Log.v("INSERT",entity.getName()+"  "+entity.getPhoneNumber());
                                progressDialog.hide();
                                Toast.makeText(getContext(),"Объявление отправлено", Toast.LENGTH_SHORT).show();
                                callback.nextAfterAddAdvert();
                            }
                            else
                            {
                                Log.v("INSERT",exception.getMessage());
                                progressDialog.hide();
                                Toast.makeText(getContext(),"Произошла ошибка, свяжитесь со службой поддержки", Toast.LENGTH_SHORT).show();
                                try {
                                    Log.v("INSERT",exception.getCause().toString());
                                }
                                catch (Exception e){

                                }

                            }
                        }
                    });
                }


            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                } else {
                    Log.e("DB", "PERMISSION GRANTED");
                }

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST);
            }
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    image.setImageURI(selectedImage);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    if (selectedImage!=null){
                        //String path = getPathFromURI(selectedImage);
                    }
                    image.setImageURI(selectedImage);
                }
                break;
        }
    }





}
