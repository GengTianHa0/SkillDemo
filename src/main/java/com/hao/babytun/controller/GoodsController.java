package com.hao.babytun.controller;


import com.hao.babytun.entity.*;
import com.hao.babytun.service.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class GoodsController {


    Logger logger = LoggerFactory.getLogger(GoodsController.class);
    @Resource
    TGoodsService tGoodsService;
    @Resource
    private TGoodsCoverService tGoodsCoverService;
    @Resource
    private TGoodsDetailService tGoodsDetailService;
    @Resource
    private TGoodsParamService tGoodsParamService;

    //freemarker的核心配置类 用于动态生成模板对象
    //在SpringBoot IOC容器初始化时候 自动Configuration就被初始化了
    @Resource
    private Configuration freemakerConfig;


    @Resource
    private  TEvaluateService tEvaluateService;


    @GetMapping("/goods")
    public ModelAndView showGoods(Long goodsId) {


        ModelAndView mv = new ModelAndView("/goods");
        TGoods tGoods = tGoodsService.getGoods(goodsId);
        if (tGoods == null) {
            return mv;
        }
        List<TGoodsCover> tGoodsCovers = tGoodsCoverService.findCoversByGoodsId(goodsId);
        List<TGoodsDetail> tGoodsDetails = tGoodsDetailService.findDetailByGoodsId(goodsId);
        List<TGoodsParam> tGoodsParams = tGoodsParamService.findParamByGoodsId(goodsId);
        mv.addObject("goods", tGoods);
        mv.addObject("covers", tGoodsCovers);
        mv.addObject("details", tGoodsDetails);
        mv.addObject("params", tGoodsParams);

        logger.info("goodsId:" + goodsId);
        return mv;
    }

    @GetMapping("/static/{gid}")
    @ResponseBody
    public String tostatic(@PathVariable Long gid) throws IOException, TemplateException {

        //动态获取模板对象
        Template template = freemakerConfig.getTemplate("goods.ftlh");

        Map parm = new HashMap();
        parm.put("goods", tGoodsService.getGoods(gid));
        parm.put("covers", tGoodsCoverService.findCoversByGoodsId(gid));
        parm.put("details",tGoodsDetailService.findDetailByGoodsId(gid));
        parm.put("params",tGoodsParamService.findParamByGoodsId(gid));

        //arg1:代表数据
        //arg2:输出位置
        File target =new File("I:\\babytun\\nginx\\goods\\"+gid+".html");
        FileWriter out=new FileWriter(target);
        template.process(parm,out);
        out.close();
        return target.getPath();
    }


    @GetMapping("/static_all")
    @ResponseBody
    public String doStatic() {
        //获取模板
        try {
            Template template = freemakerConfig.getTemplate("goods.ftlh");
            List<TGoods> tGoods = tGoodsService.getGoods();
            for (TGoods g : tGoods) {
                List<TGoodsCover> tGoodsCovers = tGoodsCoverService.findCoversByGoodsId(g.getGoodsId());
                List<TGoodsDetail> tGoodsDetails = tGoodsDetailService.findDetailByGoodsId(g.getGoodsId());
                List<TGoodsParam> tGoodsParams = tGoodsParamService.findParamByGoodsId(g.getGoodsId());
                File file = new File("I:\\babytun\\nginx\\goods\\" + g.getGoodsId() + ".html");
                FileWriter out = new FileWriter(file);
                Map map = new HashMap();
                map.put("goods", g);
                map.put("covers", tGoodsCovers);
                map.put("details", tGoodsDetails);
                map.put("params", tGoodsParams);
                template.process(map, out);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "no";
        } catch (TemplateException e) {
            e.printStackTrace();
            return "no";
        }
        return "ok";
    }


    @GetMapping("/evaluates/{goodsId}")
    @ResponseBody
    public List<TEvaluate> findEvaluates(@PathVariable("goodsId") Long goodsId){
        return tEvaluateService.findEvaluateByGoodsId(goodsId);

    }

}
