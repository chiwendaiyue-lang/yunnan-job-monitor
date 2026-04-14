package com.yunnan.jobmonitor.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

  @GetMapping("/dictionaries")
  public Map<String, List<String>> dictionaries() {
    Map<String, List<String>> m = new LinkedHashMap<>();
    m.put(
        "reduceTypes",
        List.of(
            "关闭破产",
            "停业整顿",
            "经济性裁员",
            "业务转移",
            "自然减员",
            "正常解除或终止劳动合同",
            "国际因素变化影响",
            "自然灾害",
            "重大事件影响",
            "其他"));
    m.put(
        "mainReasons",
        List.of(
            "产业结构调整",
            "重大技术改革",
            "节能减排、淘汰落后产能",
            "订单不足",
            "原材料涨价",
            "工资、社保等用工成本上升",
            "自然减员",
            "经营资金困难",
            "税收政策变化（包括税负增加或出口退税减少等）",
            "季节性用工",
            "其他",
            "自行离职",
            "工作调动、企业内部调剂",
            "劳动关系转移、劳务派遣"));
    m.put("natureLevel1", List.of("内资", "外资", "港澳台商投资"));
    m.put("natureLevel2", List.of("有限责任公司", "股份有限公司", "国有企业", "集体企业", "私营企业"));
    m.put("industryLevel1", List.of("制造业", "建筑业", "批发和零售业", "信息传输、软件和信息技术服务业"));
    m.put("industryLevel2", List.of("通用设备制造", "专用设备制造", "房屋建筑", "软件开发"));
    return m;
  }
}
