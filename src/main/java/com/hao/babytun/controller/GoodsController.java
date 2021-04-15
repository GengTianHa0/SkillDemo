package com.hao.babytun.controller;


import com.hao.babytun.entity.*;
import com.hao.babytun.service.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


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
    @Resource
    private TPromotionSeckillService tPromotionSeckillService;
    //freemarker的核心配置类 用于动态生成模板对象
    //在SpringBoot IOC容器初始化时候 自动Configuration就被初始化了
    @Resource
    private Configuration freemakerConfig;

    @Resource
    private TOrderService tOrderService;
    @Resource
    private TEvaluateService tEvaluateService;


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
        parm.put("details", tGoodsDetailService.findDetailByGoodsId(gid));
        parm.put("params", tGoodsParamService.findParamByGoodsId(gid));

        //arg1:代表数据
        //arg2:输出位置
        File target = new File("I:\\babytun\\nginx\\goods\\" + gid + ".html");
        FileWriter out = new FileWriter(target);
        template.process(parm, out);
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
    public List<TEvaluate> findEvaluates(@PathVariable("goodsId") Long goodsId) {
        return tEvaluateService.findEvaluateByGoodsId(goodsId);
    }

    @RequestMapping("/seckill/goods")
    @ResponseBody
    public Map seckillGoods(long id, String userid, @RequestParam(defaultValue = "0") int num) {
        ConcurrentHashMap map = new ConcurrentHashMap();
        try {
            //redis中减库存
            tPromotionSeckillService.SeckillGoods(id, userid, num);

   /*         如果抢到了就会继续执行接下来的程序 即把抢到的订单生成一个订单编号
             生成期间异步把订单编号和下单人给传到rabbitmq中以期望  rabbitmq进行削峰对mysql的存储*/
            String orderNo = tPromotionSeckillService.SeckillOrder(userid);
            map.put("code", "200");
            map.put("msg", "恭喜你，抢购成功");
            map.put("orderNo", orderNo);
            System.out.println("恭喜你，抢购成功");
        } catch (Exception e) {
            map.put("code", "500");
            map.put("msg", e.getMessage());
        }
        return map;
    }

    //从数据库中查询订单 如果数据库中已经创建订单则成功 否则继续等待
    @GetMapping
    public ModelAndView checkOrder(String orderNo) {
        TOrder tOrder = tOrderService.findByOrderNo(orderNo);
        ModelAndView mav = new ModelAndView();
        if (tOrder != null) {
            mav.addObject("order", orderNo);
            mav.setViewName("/order");
        } else {
            mav.addObject("orderNo", orderNo);
            mav.setViewName("/wait");
        }
        return mav;
    }


    @GetMapping("login")
    @ResponseBody
    public String login(String u, WebRequest request) {
        request.setAttribute("user", u, WebRequest.SCOPE_SESSION);
        return "port:" + ",login,success";
    }


    @GetMapping("check")
    @ResponseBody
    public String checkUser(WebRequest request) {
        String user = (String) request.getAttribute("user", WebRequest.SCOPE_SESSION);


        if (user != null) {
            return "user:" + user;
        } else {
            return "redirect to login";
        }
    }

}
