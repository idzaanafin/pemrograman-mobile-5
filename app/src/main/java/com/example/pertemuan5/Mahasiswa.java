package com.example.pertemuan5;

public class Mahasiswa {
    private final String nrp;
    private final String nama;
    private final String jurusan;
    private final String fakultas;
    private final String noHp;

    public Mahasiswa(String nrp, String nama, String jurusan, String fakultas, String noHp) {
        this.nrp = nrp;
        this.nama = nama;
        this.jurusan = jurusan;
        this.fakultas = fakultas;
        this.noHp = noHp;
    }

    public String getNrp() { return nrp; }
    public String getNama() { return nama; }
    public String getJurusan() { return jurusan; }
    public String getFakultas() { return fakultas; }
    public String getNoHp() { return noHp; }
}
