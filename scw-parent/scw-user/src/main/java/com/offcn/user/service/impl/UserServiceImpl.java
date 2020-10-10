package com.offcn.user.service.impl;

import com.offcn.user.enums.UserExceptionEnum;
import com.offcn.user.exception.UserException;
import com.offcn.user.mapper.TMemberAddressMapper;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.po.TMember;
import com.offcn.user.po.TMemberAddress;
import com.offcn.user.po.TMemberAddressExample;
import com.offcn.user.po.TMemberExample;
import com.offcn.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TMemberMapper memberMapper;

    @Autowired
    private TMemberAddressMapper addressMapper;

    @Override
    public void registerUser(TMember member) {
        //检查手机号是否存在
        TMemberExample example = new TMemberExample();
        example.createCriteria().andLoginacctEqualTo(member.getLoginacct());
        long l = memberMapper.countByExample(example);

        if (l == 1){
            //存在
            throw new UserException(UserExceptionEnum.EMAIL_EXIST);
        }
        //不存在，则注册
        //加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(member.getUserpswd());
        //注册
        member.setUserpswd(encode);
        member.setUsername(member.getLoginacct());

        //实名认证状态 0 - 未实名认证， 1 - 实名认证申请中， 2 - 已实名认证
        member.setAuthstatus("0");
        //用户类型: 0 - 个人， 1 - 企业
        member.setUsertype("0");
        //账户类型: 0 - 企业， 1 - 个体， 2 - 个人， 3 - 政府
        member.setAccttype("2");
        System.out.println("插入数据："+member.getLoginacct());
        memberMapper.insert(member);
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public TMember login(String username, String password) {

        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(username);

        List<TMember> tMembers = memberMapper.selectByExample(example);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (tMembers != null && tMembers.size() == 1){
            TMember member = tMembers.get(0);
            boolean b = encoder.matches(password, member.getUserpswd());
            return b ? member : null;
        }

        return null;
    }

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @Override
    public TMember findMemById(Integer id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TMemberAddress> addressList(Integer mid) {
        TMemberAddressExample example = new TMemberAddressExample();
        TMemberAddressExample.Criteria criteria = example.createCriteria();
        criteria.andMemberidEqualTo(mid);
        return addressMapper.selectByExample(example);
    }
}
