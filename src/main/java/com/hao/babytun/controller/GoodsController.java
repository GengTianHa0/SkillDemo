package com.hao.babytun.controller;


import com.hao.babytun.entity.TGoods;
import com.hao.babytun.entity.TGoodsCover;
import com.hao.babytun.entity.TGoodsDetail;
import com.hao.babytun.entity.TGoodsParam;
import com.hao.babytun.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import java.util.List;

@Controller
public class GoodsController {




    Logger logger= LoggerFactory.getLogger(GoodsController.class);
    @Resource
    TGoodsService tGoodsService;
    @Resource
    private TGoodsCoverService tGoodsCoverService;
    @Resource
    private TGoodsDetailService tGoodsDetailService;
    @Resource
    private TGoodsParamService tGoodsParamService;
//    @Resource
//    private TEvaluateService tEvaluateService;
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

        logger.info("goodsId:"+goodsId);
        return mv;
    }
}
