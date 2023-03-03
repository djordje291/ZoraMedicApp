package com.zoramedic.zoramedicapp.data;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.zoramedic.zoramedicapp.BuildConfig;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.view.util.Constants;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WordCreator {

    private Patient patient;
    private Activity activity;
    private HashMap<String, Integer> billMap = new HashMap<>();
    private List<Bill> billList;
    private File filePath;

    private InputStream fileInputStream;

    private HashMap<String, Integer> pharmacyMap = new HashMap<>();
    private List<Pharmacy> pharmacyList;
    
    private String start;
    private String end;

    private int sum;

    private List<Pharmacy> pharmacies = new ArrayList<>();
    private HashMap<String, Integer> perOsMap = new HashMap<>();
    private HashMap<String, Integer> parenteralnaMap = new HashMap<>();
    int rowNumber = 0;

    public WordCreator(Patient patient, Activity activity, List<Bill> billList, List<Pharmacy> pharmacyList, Date start, Date end) {
        this.patient = patient;
        this.activity = activity;
        this.billList = billList;
        this.pharmacyList = pharmacyList;
        formatDate(start, end);
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
        filePath = new File(activity.getExternalFilesDir(null), activity.getString(R.string.specifikacija_troskova_za) + " " +
                patient.getName() + ".docx");
        filePath.delete();
        try {
            filePath.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileInputStream = activity.getResources().openRawResource(R.raw.word_primer);
    }

    public void create() {
        sortAndGroupBills();

        try {
            XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
            XWPFParagraph header = xwpfDocument.createParagraph();
            XWPFRun headerRun = header.createRun();
            headerRun.setText(activity.getString(R.string.specifikacija_troskova_za) +
                    " " + patient.getName() +
                    " " + activity.getString(R.string.od) +
                    " " + start +
                    " " + activity.getString(R.string.doo) +
                    " " + end);
            headerRun.setFontSize(18);
            headerRun.setFontFamily(String.valueOf(activity.getResources().getFont(R.font.work_sans)));
            headerRun.addCarriageReturn();

            createTable(xwpfDocument);

            createPharmacyTable(xwpfDocument);

            XWPFParagraph footer = xwpfDocument.createParagraph();
            XWPFRun footerRun = footer.createRun();
            footerRun.addCarriageReturn();
            footerRun.setText("Datum i Mesto:                           Odgovorno lice:");
            footerRun.setFontSize(16);
            footerRun.setFontFamily(String.valueOf(activity.getResources().getFont(R.font.work_sans)));


            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            xwpfDocument.write(fileOutputStream);


            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            xwpfDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTable(XWPFDocument document) {

        XWPFTable table = document.createTable(billMap.size() + 1, 4);
        int rowNumber = 1;
        table.getRow(0).getCell(0).setText(activity.getString(R.string.naziv_usluge_leka));
        table.getRow(0).getCell(1).setText(activity.getString(R.string.cena));
        table.getRow(0).getCell(2).setText(activity.getString(R.string.kolicina));
        table.getRow(0).getCell(3).setText(activity.getString(R.string.ukupno));
        for (String s : billMap.keySet()) {
            for (Bill b : billList) {
                if (b.getDocRef().equals(s)) {
                    table.getRow(rowNumber).getCell(0).setText(b.getTitle());
                    table.getRow(rowNumber).getCell(1).setText(Integer.toString(b.getPrice()) + activity.getString(R.string.coma00));
                    table.getRow(rowNumber).getCell(2).setText(Integer.toString(billMap.get(s)));
                    table.getRow(rowNumber++).getCell(3).setText(Integer.toString(b.getPrice() * billMap.get(s)) + activity.getString(R.string.coma00));
                    sum += b.getPrice() * billMap.get(s);
                }
            }
        }
    }

    private void createPharmacyTable(XWPFDocument document) {
        int i = 1;
        if (perOsMap.size() > 0) {
            i++;
        }
        if (parenteralnaMap.size() > 0) {
            i++;
        }
        XWPFTable table = document.createTable(perOsMap.size() + parenteralnaMap.size() + i, 4);

        populateTable(perOsMap, R.string.per_os_th, table);
        populateTable(parenteralnaMap, R.string.parenteralna_th, table);

        table.getRow(rowNumber).getCell(0).setText(activity.getString(R.string.konacan_racun));
        table.getRow(rowNumber).getCell(3).setText(Integer.toString(sum) + activity.getString(R.string.coma00));
    }

    private void populateTable(HashMap<String, Integer> map, int string, XWPFTable table) {
        boolean flag;
        if (map.size() > 0) {
            table.getRow(rowNumber++).getCell(0).setText(activity.getString(string));
            for (String s : map.keySet()) {
                flag = false;
                for (Pharmacy p : pharmacyList) {
                    if (p.getDocRef().equals(s)) {
                        flag = true;
                        table.getRow(rowNumber).getCell(0).setText(p.getName());
                        table.getRow(rowNumber).getCell(1).setText(p.getPrice() + activity.getString(R.string.coma00));
                        table.getRow(rowNumber).getCell(2).setText(Integer.toString(map.get(p.getDocRef())));
                        table.getRow(rowNumber++).getCell(3).setText(Integer.toString(p.getPrice() * map.get(p.getDocRef())) + activity.getString(R.string.coma00));
                        sum += p.getPrice() * map.get(p.getDocRef());
                    }
                }
                if (!flag) {
                    for (Pharmacy p : pharmacies) {
                        if (p.getDocRef().equals(s)) {
                            table.getRow(rowNumber).getCell(0).setText(p.getName());
                            table.getRow(rowNumber).getCell(1).setText(p.getPrice() + activity.getString(R.string.coma00));
                            table.getRow(rowNumber).getCell(2).setText(Integer.toString(map.get(p.getDocRef())));
                            table.getRow(rowNumber++).getCell(3).setText(Integer.toString(p.getPrice() * map.get(p.getDocRef())) + activity.getString(R.string.coma00));
                            sum += p.getPrice() * map.get(p.getDocRef());
                        }
                    }
                }
            }
        }
    }

    private void sortAndGroupBills() {
        for (Service service : patient.getServices()) {
            sortAndGroupPharmacy(service);
            if (billMap.containsKey(service.getBill().getDocRef())) {
                billMap.put(service.getBill().getDocRef(), billMap.get(service.getBill().getDocRef()) + 1);
            } else {
                billMap.put(service.getBill().getDocRef(), 1);
            }
        }
    }

    private void sortAndGroupPharmacy(Service service) {
        if (service.getPharmacyList() != null && service.getPharmacyList().size() != 0) {
            for (Pharmacy p : service.getPharmacyList()) {
                if (pharmacies.size() > 0) {
                    for (Pharmacy ps : pharmacies) {
                        if (ps.getDocRef().equals(p.getDocRef())) {
                            pharmacies.remove(ps);
                            pharmacies.add(p);
                        }
                    }
                } else {
                    pharmacies.add(p);
                }
                if (p.isPerOs()) {
                    if (perOsMap.containsKey(p.getDocRef())) {
                        perOsMap.put(p.getDocRef(), perOsMap.get(p.getDocRef()) + p.getUsed());
                    } else {
                        perOsMap.put(p.getDocRef(), p.getUsed());
                    }
                } else {
                    if (parenteralnaMap.containsKey(p.getDocRef())) {
                        parenteralnaMap.put(p.getDocRef(), parenteralnaMap.get(p.getDocRef()) + p.getUsed());
                    } else {
                        parenteralnaMap.put(p.getDocRef(), p.getUsed());
                    }

                }
            }
        }
    }

    public void openFile() {
        Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", filePath);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/msword");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(intent);
    }
    
    private void formatDate(Date start, Date end) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.MINI_DATE_FORMAT);
        this.start = sdf.format(start);
        this.end = sdf.format(end);
    }
}
