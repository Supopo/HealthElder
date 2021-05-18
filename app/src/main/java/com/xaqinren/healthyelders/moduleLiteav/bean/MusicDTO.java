package com.xaqinren.healthyelders.moduleLiteav.bean;

import java.util.List;

public class MusicDTO {
    public String musicId;
    public String musicName;
    public String intro;
    public String albumId;
    public String albumName;
    public Integer duration;
    public Integer bpm;
    public Integer auditionBegin;
    public Integer auditionEnd;
    public List<?> tag;
    public List<CoverDTO> cover;
    public List<ArtistDTO> artist;
    public List<AuthorDTO> author;
    public List<ComposerDTO> composer;
    public List<?> arranger;
    public List<VersionDTO> version;
}
