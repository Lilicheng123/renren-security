package io.renren.modules.transactionRecords.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 
 * @author llc
 * @email 787254039@qq.com
 * @date 2019-05-02 16:26:52
 */
@TableName("transaction_records")
public class TransactionRecordsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 自增id
	 */
	@TableId
	private Integer id;
	/**
	 * 股票名称
	 */
	private String name;
	/**
	 * 交易数量
	 */
	private Integer nums;
	/**
	 * 1：买入，2：卖出
	 */
	private Integer status;
	/**
	 * 卖出时间
	 */
	private Date saleTime;
	/**
	 * 买入时间
	 */
	private Date buyTime;
	/***
	 * 买入价格
	 */
	private BigDecimal buyPrice;

	/***
	 * 卖出价格
	 */
	private BigDecimal salePrice;

	/**
	 * 设置：自增id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：自增id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：股票名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：股票名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：交易数量
	 */
	public void setNums(Integer nums) {
		this.nums = nums;
	}
	/**
	 * 获取：交易数量
	 */
	public Integer getNums() {
		return nums;
	}
	/**
	 * 设置：1：买入，2：卖出
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：1：买入，2：卖出
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置：卖出时间
	 */
	public void setSaleTime(Date saleTime) {
		this.saleTime = saleTime;
	}
	/**
	 * 获取：卖出时间
	 */
	public Date getSaleTime() {
		return saleTime;
	}
	/**
	 * 设置：买入时间
	 */
	public void setBuyTime(Date buyTime) {
		this.buyTime = buyTime;
	}
	/**
	 * 获取：买入时间
	 */
	public Date getBuyTime() {
		return buyTime;
	}

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}
}
