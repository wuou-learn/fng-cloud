//@version=4
strategy(title="My Strategy", overlay=true, pyramiding=0, calc_on_every_tick=false, default_qty_type=strategy.percent_of_equity, default_qty_value=0.1, initial_capital=1000, commission_type=strategy.commission.percent, commission_value=0.02, slippage=1)
// 仓位模式 - 百分比
// 仓位
// 初始资金
// 手续费收费模式 - 百分比
// 手续费

poiSize = input(defval=50, title="仓位控制%", type=input.float, minval=10, maxval=100, step=5)

plot(ema(close, 12), color = #ff007a ,linewidth=2)
plot(ema(close, 100), color = #f57f17 ,linewidth=2)
plot(ema(close, 200), color = #2962ff ,linewidth=3)


ema_100 = ema(close, 100)
vrsi = rsi(close, 14)

avg_price_144 = sma(close, 144)
bias_144 = (close - avg_price_144) / avg_price_144

avg_price_88 = sma(close, 88)
bias_88 = (close - avg_price_88) / avg_price_88


//upper = close[0] > ema_100 and vrsi < 30 and bias_144 > 0
//lower = close[0] < ema_100 and vrsi > 60 and bias_144 < 0
lower = vrsi <=80 and vrsi >= 55 and bias_88 <= 1
upper = vrsi <= 35 and vrsi >= 20 and bias_88 >= -0.25
// 计算开仓数量
openPositions = abs(strategy.equity / close[0]) * (poiSize / 100)

// 测试的 K线 数量
ebar = input(defval=300, title="测试的历史 K线 个数", minval=0)

// 计算时间周期
tdays = (timenow - time) / 60000.0
tdays := tdays / timeframe.multiplier
if timeframe.ismonthly
    tdays := tdays / 1440.0 / 5.0 / 4.3 / timeframe.multiplier
    tdays
if timeframe.isweekly
    tdays := tdays / 1440.0 / 5.0 / timeframe.multiplier
    tdays
if timeframe.isdaily
    tdays := tdays / 1440.0 / timeframe.multiplier
    tdays

// 在测试的 k线 范围内, 设置进场和出场条件
if ebar == 0 or tdays <= ebar
    strategy.entry("upper", strategy.long, openPositions, when=upper == true, comment = "多")
    strategy.entry("lower", strategy.short, openPositions, when=lower == true, comment = "空")
    strategy.exit("upper", from_entry="upper", profit=na, loss=na)
    strategy.exit("lower", from_entry="lower", profit=na, loss=na)