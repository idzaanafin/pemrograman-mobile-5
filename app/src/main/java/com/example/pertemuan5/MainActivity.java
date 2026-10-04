package com.example.pertemuan5;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText edtSearchNrp, nrp, nama, jurusan, fakultas, noHp;
    private TextInputLayout layoutSearchNrp, layoutNrp, layoutNama, layoutJurusan, layoutFakultas, layoutNoHp;
    private SQLiteDatabase dbku;
    private MahasiswaAdapter mahasiswaAdapter;
    private View detailCard;
    private View actionsCard;
    private final ArrayList<Mahasiswa> daftarMahasiswa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSearchNrp = findViewById(R.id.edtSearchNrp);
        layoutSearchNrp = findViewById(R.id.layoutSearchNrp);
        nrp = findViewById(R.id.nrp);
        nama = findViewById(R.id.nama);
        jurusan = findViewById(R.id.jurusan);
        fakultas = findViewById(R.id.fakultas);
        noHp = findViewById(R.id.no_hp);
        layoutNrp = findViewById(R.id.layoutNrp);
        layoutNama = findViewById(R.id.layoutNama);
        layoutJurusan = findViewById(R.id.layoutJurusan);
        layoutFakultas = findViewById(R.id.layoutFakultas);
        layoutNoHp = findViewById(R.id.layoutNoHp);

        ListView listMahasiswa = findViewById(R.id.listMahasiswa);
        detailCard = findViewById(R.id.detailCard);
        actionsCard = findViewById(R.id.actionsCard);
        mahasiswaAdapter = new MahasiswaAdapter(this, daftarMahasiswa);
        listMahasiswa.setAdapter(mahasiswaAdapter);
        listMahasiswa.setOnItemClickListener((parent, view, position, id) -> tampilkanDetail(daftarMahasiswa.get(position)));

        dbku = new DbHelper(this).getWritableDatabase();
        muatDataDariDatabase();

        findViewById(R.id.btnTambah).setOnClickListener(v -> tampilkanDialogTambah());
        findViewById(R.id.btnSearch).setOnClickListener(v -> cari());
        findViewById(R.id.btnSimpan).setOnClickListener(v -> simpan());
        findViewById(R.id.btnUpdate).setOnClickListener(v -> update());
        findViewById(R.id.btnHapus).setOnClickListener(v -> hapus());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearForm());
        edtSearchNrp.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                cari();
                return true;
            }
            return false;
        });
    }

    private void tampilkanDetail(Mahasiswa mahasiswa) {
        nrp.setText(mahasiswa.getNrp());
        nama.setText(mahasiswa.getNama());
        jurusan.setText(mahasiswa.getJurusan());
        fakultas.setText(mahasiswa.getFakultas());
        noHp.setText(mahasiswa.getNoHp());
        detailCard.setVisibility(View.VISIBLE);
        actionsCard.setVisibility(View.VISIBLE);
    }

    private void tampilkanDialogTambah() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_mahasiswa, null);
        EditText dialogNrp = dialogView.findViewById(R.id.dialogNrp);
        EditText dialogNama = dialogView.findViewById(R.id.dialogNama);
        EditText dialogJurusan = dialogView.findViewById(R.id.dialogJurusan);
        EditText dialogFakultas = dialogView.findViewById(R.id.dialogFakultas);
        EditText dialogNoHp = dialogView.findViewById(R.id.dialogNoHp);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Tambah Data Mahasiswa")
                .setView(dialogView)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", null)
                .create();

        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String valueNrp = dialogNrp.getText().toString().trim();
            String valueNama = dialogNama.getText().toString().trim();
            String valueJurusan = dialogJurusan.getText().toString().trim();
            String valueFakultas = dialogFakultas.getText().toString().trim();
            String valueNoHp = dialogNoHp.getText().toString().trim();

            if (valueNrp.length() != 10 || !valueNrp.matches("\\d{10}")) {
                dialogNrp.setError("NRP harus 10 digit angka");
                return;
            }
            if (valueNama.isEmpty() || valueJurusan.isEmpty() || valueFakultas.isEmpty() || valueNoHp.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues data = buatContentValues(valueNrp, valueNama, valueJurusan, valueFakultas, valueNoHp);
            if (dbku.insert("mhs", null, data) == -1) {
                Toast.makeText(this, "NRP sudah terdaftar", Toast.LENGTH_SHORT).show();
                return;
            }
            muatDataDariDatabase();
            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }));
        dialog.show();
    }

    private void muatDataDariDatabase() {
        daftarMahasiswa.clear();
        try (Cursor cursor = dbku.query("mhs", null, null, null, null, null, "nrp ASC")) {
            while (cursor.moveToNext()) {
                daftarMahasiswa.add(new Mahasiswa(
                        cursor.getString(cursor.getColumnIndexOrThrow("nrp")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nama")),
                        cursor.getString(cursor.getColumnIndexOrThrow("jurusan")),
                        cursor.getString(cursor.getColumnIndexOrThrow("fakultas")),
                        cursor.getString(cursor.getColumnIndexOrThrow("no_hp"))));
            }
        }
        if (mahasiswaAdapter != null) {
            mahasiswaAdapter.notifyDataSetChanged();
        }
    }

    private ContentValues buatContentValues(String valueNrp, String valueNama, String valueJurusan,
                                             String valueFakultas, String valueNoHp) {
        ContentValues data = new ContentValues();
        data.put("nrp", valueNrp);
        data.put("nama", valueNama);
        data.put("jurusan", valueJurusan);
        data.put("fakultas", valueFakultas);
        data.put("no_hp", valueNoHp);
        return data;
    }

    private boolean validateNrp(EditText editText, TextInputLayout inputLayout) {
        String value = editText.getText().toString().trim();
        if (!value.matches("\\d{10}")) {
            inputLayout.setError("NRP harus berupa 10 digit angka");
            return false;
        }
        inputLayout.setError(null);
        return true;
    }

    private boolean validateAllFields() {
        boolean valid = validateNrp(nrp, layoutNrp);
        valid = validateText(nama, layoutNama, "Nama") && valid;
        valid = validateText(jurusan, layoutJurusan, "Jurusan") && valid;
        valid = validateText(fakultas, layoutFakultas, "Fakultas") && valid;
        valid = validateText(noHp, layoutNoHp, "Nomor HP") && valid;
        return valid;
    }

    private boolean validateText(EditText field, TextInputLayout layout, String label) {
        if (field.getText().toString().trim().isEmpty()) {
            layout.setError(label + " tidak boleh kosong");
            return false;
        }
        layout.setError(null);
        return true;
    }

    private void cari() {
        if (!validateNrp(edtSearchNrp, layoutSearchNrp)) {
            showToast("Masukkan NRP 10 digit pada pencarian");
            return;
        }
        String valueNrp = edtSearchNrp.getText().toString().trim();
        try (Cursor cursor = dbku.query("mhs", null, "nrp = ?", new String[]{valueNrp}, null, null, null)) {
            if (!cursor.moveToFirst()) {
                layoutSearchNrp.setError("Data tidak ditemukan");
                return;
            }
            nrp.setText(cursor.getString(cursor.getColumnIndexOrThrow("nrp")));
            nama.setText(cursor.getString(cursor.getColumnIndexOrThrow("nama")));
            jurusan.setText(cursor.getString(cursor.getColumnIndexOrThrow("jurusan")));
            fakultas.setText(cursor.getString(cursor.getColumnIndexOrThrow("fakultas")));
            noHp.setText(cursor.getString(cursor.getColumnIndexOrThrow("no_hp")));
            showToast("Data ditemukan");
        }
    }

    private void simpan() {
        if (!validateAllFields()) {
            showToast("Harap lengkapi semua field");
            return;
        }
        String valueNrp = nrp.getText().toString().trim();
        long result = dbku.insert("mhs", null, buatContentValues(valueNrp,
                nama.getText().toString().trim(), jurusan.getText().toString().trim(),
                fakultas.getText().toString().trim(), noHp.getText().toString().trim()));
        if (result == -1) {
            layoutNrp.setError("NRP ini sudah terdaftar");
        } else {
            edtSearchNrp.setText(valueNrp);
            muatDataDariDatabase();
            showToast("Data berhasil disimpan");
        }
    }

    private void update() {
        if (!validateAllFields()) {
            showToast("Harap lengkapi semua field");
            return;
        }
        String valueNrp = nrp.getText().toString().trim();
        ContentValues data = buatContentValues(valueNrp, nama.getText().toString().trim(),
                jurusan.getText().toString().trim(), fakultas.getText().toString().trim(), noHp.getText().toString().trim());
        int rows = dbku.update("mhs", data, "nrp = ?", new String[]{valueNrp});
        if (rows > 0) {
            muatDataDariDatabase();
            showToast("Data berhasil di-update");
        } else {
            showToast("NRP tidak ditemukan");
        }
    }

    private void hapus() {
        if (!validateNrp(nrp, layoutNrp)) {
            return;
        }
        String valueNrp = nrp.getText().toString().trim();
        if (dbku.delete("mhs", "nrp = ?", new String[]{valueNrp}) > 0) {
            clearForm();
            muatDataDariDatabase();
            showToast("Data berhasil dihapus");
        } else {
            showToast("NRP tidak ditemukan");
        }
    }

    private void clearForm() {
        edtSearchNrp.setText("");
        nrp.setText("");
        nama.setText("");
        jurusan.setText("");
        fakultas.setText("");
        noHp.setText("");
        layoutSearchNrp.setError(null);
        layoutNrp.setError(null);
        layoutNama.setError(null);
        layoutJurusan.setError(null);
        layoutFakultas.setError(null);
        layoutNoHp.setError(null);
        detailCard.setVisibility(View.GONE);
        actionsCard.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (dbku != null && dbku.isOpen()) {
            dbku.close();
        }
        super.onDestroy();
    }
}
