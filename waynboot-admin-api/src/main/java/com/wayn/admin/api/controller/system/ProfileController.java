package com.wayn.admin.api.controller.system;

import com.wayn.admin.framework.security.service.TokenService;
import com.wayn.common.base.service.UploadService;
import com.wayn.common.config.WaynConfig;
import com.wayn.common.core.domain.system.User;
import com.wayn.common.core.model.LoginUserDetail;
import com.wayn.common.core.service.system.IUserService;
import com.wayn.common.util.R;
import com.wayn.common.util.ServletUtils;
import com.wayn.common.util.file.FileUploadUtil;
import com.wayn.common.util.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("system/user/profile")
public class ProfileController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UploadService uploadService;

    @GetMapping
    public R profile() {
        R success = R.success();
        LoginUserDetail loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        success.add("user", loginUser.getUser());
        success.add("roleGroup", iUserService.selectUserRoleGroup(loginUser.getUsername()));
        return success;
    }

    @PreAuthorize("@ss.hasPermi('system:profile:update')")
    @PutMapping
    public R updateProfile(@RequestBody User user) {
        if (iUserService.updateById(user)) {
            LoginUserDetail loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            // 更新缓存用户信息
            loginUser.getUser().setNickName(user.getNickName());
            loginUser.getUser().setPhone(user.getPhone());
            loginUser.getUser().setEmail(user.getEmail());
            loginUser.getUser().setSex(user.getSex());
            tokenService.refreshToken(loginUser);
            return R.success();
        }
        return R.error("修改个人信息异常，请联系管理员");
    }

    @PreAuthorize("@ss.hasPermi('system:profile:update')")
    @PutMapping("/updatePwd")
    public R updatePwd(String oldPassword, String newPassword) {
        LoginUserDetail loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String password = loginUser.getPassword();

        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return R.error("旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return R.error("新密码不能与旧密码相同");
        }
        boolean result = iUserService.update().set("password", SecurityUtils.encryptPassword(newPassword)).update();
        if (result) {
            // 更新缓存用户信息
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.refreshToken(loginUser);
            return R.success();
        }
        return R.error("修改密码异常，请联系管理员");
    }

    @PreAuthorize("@ss.hasPermi('system:profile:update')")
    @PostMapping("/avatar")
    public R avatar(@RequestParam("avatarfile") MultipartFile file, HttpServletRequest request) throws IOException {
        if (!file.isEmpty()) {
            LoginUserDetail loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            String filename = FileUploadUtil.uploadFile(file, WaynConfig.getUploadDir());
            String fileUrl = uploadService.uploadFile(filename);
            boolean result = iUserService.update().set("avatar", fileUrl).eq("user_name", loginUser.getUsername()).update();
            if (result) {
                R success = R.success();
                success.add("imgUrl", fileUrl);
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(fileUrl);
                tokenService.refreshToken(loginUser);
                return success;
            }
        }
        return R.error("上传图片异常，请联系管理员");
    }
}
