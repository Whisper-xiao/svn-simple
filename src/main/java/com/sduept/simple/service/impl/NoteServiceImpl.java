package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.entity.Note;
import com.sduept.simple.mapper.NoteMapper;
import com.sduept.simple.service.NoteService;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {
}
