package me.jennachoi.to_doapp.Model;

public class Data {
    private String title;
    private String note;
    private String date;
    private String id;
    private String process;

    public Data() {

    }

    public Data(String title, String note, String date, String id, String process){
        this.title = title;
        this.note = note;
        this.date = date;
        this.id = id;
        this.process = process;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getProcess() { return process; }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProcess(String process) { this.process = process; }

}