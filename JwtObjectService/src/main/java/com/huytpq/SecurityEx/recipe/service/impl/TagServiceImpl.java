package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.TagInput;
import com.huytpq.SecurityEx.recipe.dto.output.TagOutput;
import com.huytpq.SecurityEx.recipe.entity.Tag;
import com.huytpq.SecurityEx.recipe.mapper.ModelMapper;
import com.huytpq.SecurityEx.recipe.repo.TagRepo;
import com.huytpq.SecurityEx.recipe.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TagOutput> getList(String name) {
        List<Tag> tags = tagRepo.findByCondition(name);
        return modelMapper.convertToTagOutputs(tags);
    }

    @Override
    public TagOutput get(Long id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.TAG_NOT_FOUND));
        return modelMapper.convertToTagOutput(tag);
    }

    @Override
    public TagOutput create(TagInput input) {
        if (tagRepo.findByName(input.getName()).isPresent()) {
            throw new BaseException(ErrorCode.TAG_NAME_EXISTED);
        }
        Tag tag = modelMapper.createTag(input);
        tag = tagRepo.save(tag);
        return modelMapper.convertToTagOutput(tag);
    }

    @Override
    public TagOutput update(Long id, TagInput input) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.TAG_NOT_FOUND));
        if (tagRepo.findByNameAndIdNot(input.getName(), id).isPresent()) {
            throw new BaseException(ErrorCode.TAG_NAME_EXISTED);
        }
        Tag updatedTag = modelMapper.updateTag(tag, input);
        tagRepo.save(updatedTag);
        return null;
    }

    @Override
    public void delete(Long id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.TAG_NOT_FOUND));
        tagRepo.delete(tag);
    }
}