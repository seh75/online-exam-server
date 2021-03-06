package com.yb.onlineexamserver.service.teacher.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yb.onlineexamserver.common.enums.majorenums.MajorEnums;
import com.yb.onlineexamserver.common.enums.subjectenums.SubjectEnums;
import com.yb.onlineexamserver.common.exception.OnlineExamException;
import com.yb.onlineexamserver.dao.MajorDao;
import com.yb.onlineexamserver.vo.MajorVo;
import com.yb.onlineexamserver.vo.MajorSimpleVo;
import com.yb.onlineexamserver.mbg.mapper.MajorMapper;
import com.yb.onlineexamserver.mbg.mapper.SubjectMapper;
import com.yb.onlineexamserver.mbg.model.Major;
import com.yb.onlineexamserver.mbg.model.Subject;
import com.yb.onlineexamserver.requestparams.MajorParams;
import com.yb.onlineexamserver.service.teacher.MajorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Auther: Yang
 * @Date: 2019/11/9 13:56
 * @Description:
 */
@Slf4j
@Service
public class MajorServiceImpl implements MajorService {

    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private MajorMapper majorMapper;
    @Autowired
    private MajorDao majorDao;

    @Override
    public int insertMajors(MajorParams majorParams) {
        log.info("新增major");
        //判断科目id是否存在
        Integer subjectId = majorParams.getSubjectId();
        Subject subject = subjectMapper.selectByPrimaryKey(subjectId);
        if(subject == null){
            throw new OnlineExamException(SubjectEnums.SUBJECT_NOT_FOUND);
        }
        Major major = new Major();
        BeanUtil.copyProperties(majorParams, major);
        major.setCreateTime(LocalDateTime.now());
        major.setUpdateTime(LocalDateTime.now());
        return majorMapper.insertSelective(major);
    }

    @Override
    public int deleteMajors(Integer id) {
        log.info("删除major:"+id);
        queryMajorById(id);
        //查询此major下是否有course存在
        //todo:
        return majorMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Major queryMajorById(Integer id) {
        log.info("查询major:"+id);
        Major major = majorMapper.selectByPrimaryKey(id);
        if(major == null){
            throw new OnlineExamException(MajorEnums.MAJOR_NOT_FOUND);
        }
        return major;
    }

    @Override
    public int updateMajors(Integer id, MajorParams majorParams) {
        log.info("更新major:"+id);
        queryMajorById(id);
        Major major = new Major();
        BeanUtil.copyProperties(majorParams, major);
        major.setId(id);
        major.setUpdateTime(LocalDateTime.now());
        return majorMapper.updateByPrimaryKeySelective(major);
    }

    @Override
    public Page<MajorVo> queryMajors(String name, Integer subjectId, Integer page, Integer pageSize) {
//        MajorExample example = new MajorExample();
//        if(!StringUtils.isEmpty(name)){
//            example.createCriteria().andNameLike("%"+name+"%");
//        }
        PageHelper.startPage(page, pageSize);
        Page<MajorVo> majorVos = (Page<MajorVo>) majorDao.queryMajors(name,subjectId);
//        List<MajorVo> majorDtoList = majorVos.stream().filter(majorDto -> {
//            if (!StringUtils.isEmpty(name)) {
//                return majorDto.getName().contains(name);
//            }
//            if (subjectId != null) {
//                return (majorDto.getSubjectId() == subjectId);
//            }
//            return true;
//        }).collect(Collectors.toList());


//        Page<Major> majors = (Page<Major>) majorMapper.selectByExample(example);
//        Page<MajorVo> majorVos = (Page<MajorVo>) majors.stream().map(major -> convert(major)).collect(Collectors.toList());
        return majorVos;
    }

    @Override
    public List<MajorSimpleVo> queryMajorsSimple() {
       return majorDao.queryMajorsSimple();
    }

}
