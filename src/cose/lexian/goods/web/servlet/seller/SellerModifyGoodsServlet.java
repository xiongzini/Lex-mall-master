package cose.lexian.goods.web.servlet.seller;

import cn.itcast.commons.CommonUtils;
import cose.lexian.goods.domain.Goods;
import cose.lexian.goods.service.GoodsService;
import cose.lexian.util.GoodsException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "SellerModifyGoodsServlet", urlPatterns = "/seller/SellerModifyGoodsServlet")
public class SellerModifyGoodsServlet extends HttpServlet {
    private GoodsService goodsService = new GoodsService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Goods goods = new Goods();
        /**1.把表单数据封装到Good对象*/
        //创建工厂
        DiskFileItemFactory factory = new DiskFileItemFactory(100*1024, new File("D:/temp"));//设置缓存大小和临时目录
        //得到解析器
        ServletFileUpload sfu = new ServletFileUpload(factory);
        //设置单个文件最大值为100kb
        sfu.setFileSizeMax(100*1024);
        //使用sfu去解析request对象，得到List<FileItem>
        try {
            List<FileItem> fileItemList = sfu.parseRequest(request);
            Map<String, String> map = new HashMap<String, String>();
            for (FileItem fileItem : fileItemList) {
                if (fileItem.isFormField()) {
                    map.put(fileItem.getFieldName(), fileItem.getString("utf-8"));
                }
            }
            goods = CommonUtils.toBean(map, Goods.class);
            goods.setG_updateTime(new Date());

            //判断表单是否为空
            if (goods.getG_name().trim().equals("")) {
                throw new GoodsException("商品名称不能为空！");
            }
            if (goods.getG_price() == 0.0) {
                throw new GoodsException("商品价格不能为空！");
            }
            if (goods.getG_count() == 0) {
                throw new GoodsException("商品库存不能为空！");
            }
            if (goods.getG_desc().trim().equals("")) {
                throw new GoodsException("商品描述不能为空！");
            }

            String old_image = goodsService.findGoodsById(goods.getG_id()).getG_image();
            if (fileItemList.get(1).getName() != "") {
                /** 2.保存上传的文件：保存路径和文件名称 */
                String str = request.getSession().getServletContext().getRealPath("/");
                int loc = str.indexOf("out");
                String savepath = str.substring(0, loc) + "goods_img";
                //得到文件名称：直接利用uuid命名，避免文件名冲突
                int begin = fileItemList.get(1).getName().indexOf(".");
                String suffix = fileItemList.get(1).getName().substring(begin); //截取图片后缀
                String filename = CommonUtils.uuid() + suffix;

                if (!(filename.toLowerCase().endsWith("png") || filename.toLowerCase().endsWith("jpg"))) {
                    goods.setG_image(old_image);
                    request.setAttribute("goods", goods);
                    request.setAttribute("msg", "您上传的图片必须是png或jpg格式！");
                    request.getRequestDispatcher("/sellerjsps/seller/goods-add.jsp").forward(request, response);
                    return;
                }

                //使用目录和文件名称创建目标文件
                File destFile = new File(savepath, filename);
                //保存上传文件到目标文件位置
                fileItemList.get(1).write(destFile);

                /** 3.设置Goods对象的image，即把图片的路径设置给Goods的image */
                goods.setG_image("/goods_img/" + filename);

                /** 校验图片的尺寸 */
                Image image = new ImageIcon(destFile.getAbsolutePath()).getImage();
                if (image.getWidth(null) != image.getHeight(null)) {
                    destFile.delete(); //删除这个文件
                    request.setAttribute("goods", goods);
                    request.setAttribute("msg", "上传图片的尺寸必须为1:1");
                    request.getRequestDispatcher("/sellerjsps/seller/goods-add.jsp").forward(request, response);
                    return;
                }
                if (image.getWidth(null) > 200 || image.getHeight(null) > 200) {
                    destFile.delete(); //删除这个文件
                    request.setAttribute("goods", goods);
                    request.setAttribute("msg", "你上传的图片尺寸超出了200*200!");
                    request.getRequestDispatcher("/sellerjsps/seller/goods-add.jsp").forward(request, response);
                    return;
                }

                /** 4.使用goodsService完成保存 */
                goodsService.modifyGoods(goods);

                /** 删除原有图片 */
                String[] strs = old_image.split("/");
                String path = "D:\\Lex-mall-master\\web\\goods_img\\" + strs[strs.length-1];
                System.out.println(path);
                //取出文件的绝对路径，然后用File方法删除相应文件。
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
            } else {
                goods.setG_image(old_image);
                goodsService.modifyGoods(goods);
            }

            /** 5.转发 */
            request.setAttribute("msg", "商品修改成功！");
            request.setAttribute("goods", goods);
            request.getRequestDispatcher("/sellerjsps/seller/goods-modify.jsp").forward(request, response);
        } catch (GoodsException e) {
            request.setAttribute("goods", goods);
            request.setAttribute("msg", e.getMessage());
            request.getRequestDispatcher("/sellerjsps/seller/goods-modify.jsp").forward(request, response);
        } catch (Exception e){
            if(e instanceof FileUploadBase.FileSizeLimitExceededException) {
                request.setAttribute("goods", goods);
                request.setAttribute("msg", "您上传的文件大小超出了100KB");
                request.getRequestDispatcher("/sellerjsps/seller/goods-modify.jsp").forward(request, response);
            }
        }

    }
}
