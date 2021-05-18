package com.xaqinren.healthyelders.moduleLiteav.bean;

import java.util.List;

public class RecordDTO {
    public Integer sheetId;
    public String sheetName;
    public String musicId;
    public String intro;
    public String musicName;
    public String albumId;
    public String albumName;
    public String duration;
    public String bpm;
    public String auditionBegin;
    public String auditionEnd;
    public Integer musicTotal;
    public Integer type;
    public String describe;
    public Integer free;
    public Integer price;
    public List<TagDTO> tag;
    public List<MusicDTO> music;
    public List<CoverDTO> cover;
    public List<ArtistDTO> artist;
    public List<AuthorDTO> author;
    public List<ComposerDTO> composer;
    public List<ArrangerDTO> arranger;
    public List<VersionDTO> version;
}
