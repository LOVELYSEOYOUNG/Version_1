package com.example.myapplication.bottomnavigation;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class FragSearch extends Fragment {
    private View view;

    private static final String TAG = "MainActivity";
    String API_KEY = "58891361-85af-4d37-bca6-be0dea50fe7f";
    ArrayList<Map<String, String>> fruits = new ArrayList<Map<String, String>>();
    boolean b = true;
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    String getText(String u) {
        String text = "";
        try {
            URL url = new URL(u);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            text = readStream(in);
            urlConnection.disconnect();
        } catch (IOException ie) {
            System.out.println("Exception is " + ie);
        }
        return text;
    }
    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue == null)
            return null;
        return nValue.getNodeValue();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.frag_search,container,false);


        //api에서 데이터 가져오고 파싱
        //api에서 데이터 가져오고 파싱

        Log.d(TAG, "dfadsfdfda");

        new Thread(() -> {
            Log.d(TAG, "dfadsf");
            String url_text = "https://www.kamis.or.kr/service/price/xml.do?action=dailyPriceByCategoryList&p_product_cls_code=02&p_country_code=1101&" +
                    "p_regday=2023-06-23&" +
                    "p_convert_kg_yn=N&" +
                    "p_item_category_code=400&" +
                    "p_cert_key=" + API_KEY + "&p_cert_id=222&p_returntype=xml";
            String text = getText(url_text);
//            System.out.println(text);

            Document doc = convertStringToDocument(text);

            //필요한정보 뽑아내는 코드 구현;

//            doc.getDocumentElement().normalize();
//            System.out.println("Root element: " + doc.getDocumentElement().getNodeName()); // Root element: result

            NodeList nList = doc.getElementsByTagName("item");
            System.out.println("파싱할 리스트 수 : "+ nList.getLength());  // 파싱할 리스트 수 :  5

            for(int temp = 1; temp < nList.getLength(); temp++){
                Node nNode = nList.item(temp);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){

                    Element eElement = (Element) nNode;
                    if (getTagValue("rank",eElement).contains("중품") || getTagValue("rank",eElement).contains("M과")) continue;
                    System.out.println("######################");
                    System.out.println("상품 이름  : " + getTagValue("item_name", eElement));
                    System.out.println("상품 코드  : " + getTagValue("item_code", eElement));
                    System.out.println("당일 가격: " + getTagValue("dpr1", eElement));
                    System.out.println("1일 전 가격: " + getTagValue("dpr2", eElement));
                    System.out.println("1주일 전 가격: " + getTagValue("dpr3", eElement));

                    Map<String, String> fruit_info = new HashMap<String, String>();
                    fruit_info.put("item_name", getTagValue("item_name", eElement));
                    fruit_info.put("item_code", getTagValue("item_code", eElement));
                    fruit_info.put("dpr1", getTagValue("dpr1", eElement));
                    fruit_info.put("dpr2", getTagValue("dpr2", eElement));
                    fruit_info.put("dpr3", getTagValue("dpr3", eElement));
                    fruits.add(fruit_info);
                }	// for end
            }	// if end
            b = false;

        }).start();

        while(b){}
        System.out.println(fruits);

        List<Integer> drawables = Arrays.asList(R.drawable.apple, R.drawable.pear,
                R.drawable.mandarin, R.drawable.banana, R.drawable.kiwi, R.drawable.pineapple,
                R.drawable.orange, R.drawable.lemon, R.drawable.cherry, R.drawable.mango);

        //===== 테스트를 위한 더미 데이터 생성 ===================
        ArrayList<DataModel> testDataSet = new ArrayList<>();

        for(int i=0; i<drawables.size(); i++){
            String item_name = fruits.get(i).get("item_name");

            String dpr1 = fruits.get(i).get("dpr1");
            String dpr2 = fruits.get(i).get("dpr2");
            float d1 = Integer.parseInt(dpr1.replace(",",""));
            float d2 = Integer.parseInt(dpr2.replace(",",""));
            float rate = (d1-d2) / d2;

            String change;
            if(rate>0) change="UP";
            else change="DOWN";

            testDataSet.add(new DataModel(item_name, drawables.get(i), dpr1, change));
        }



        //========================================================

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        recyclerView.setLayoutManager(linearLayoutManager);  // LayoutManager 설정

        DBHelper DB = new DBHelper(getActivity());
        //DBHelper DB = new DBHelper(this);
        CustomAdapter customAdapter = new CustomAdapter(testDataSet, DB);
        recyclerView.setAdapter(customAdapter); // 어댑터 설정

        ImageView bag = view.findViewById(R.id.btnView);
        bag.setImageResource(R.drawable.cart);

        bag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = DB.getdata();
                if(res.getCount()==0){
                    Toast.makeText(bag.getContext(), "No Entry Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append(res.getString(0)+"\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(bag.getContext());
                builder.setCancelable(true);
                builder.setTitle("나의 찜목록");
                builder.setMessage(buffer.toString());
                builder.show();
            }        });

        ArrayList<DataModel> search_list = new ArrayList<>();

        EditText editText;

        editText = view.findViewById(R.id.editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editText.getText().toString();
                search_list.clear();

                if(searchText.equals("")){
                    customAdapter.setItems(testDataSet);
                }
                else {
                    // 검색 단어를 포함하는지 확인
                    for (int a = 0; a < testDataSet.size(); a++) {
                        if (testDataSet.get(a).title.toLowerCase().contains(searchText.toLowerCase())) {
                            search_list.add(testDataSet.get(a));
                        }
                        customAdapter.setItems(search_list);
                    }
                }
            }
        });
        return view;
    }











    private Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
