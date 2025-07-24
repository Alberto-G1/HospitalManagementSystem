package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Bill;
import org.medcare.models.Bill.BillStatus;
import org.medcare.service.interfaces.BillServiceInterface;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class PaymentBean implements Serializable {

    @Inject
    private BillServiceInterface billService;

    // --- STATS ---
    private BigDecimal totalRevenue;
    private BigDecimal totalOutstanding;
    private long paidBillsCount;
    private long partiallyPaidBillsCount;
    private long unpaidBillsCount;

    // --- CHARTS ---
    private BarChartModel financialSummaryBarChart;
    private LineChartModel monthlyRevenueLineChart;

    @PostConstruct
    public void init() {
        List<Bill> allBills = billService.getAllBills();
        calculateStatistics(allBills);
        createFinancialSummaryBarChart();
        createMonthlyRevenueLineChart(allBills);
    }

    private void calculateStatistics(List<Bill> allBills) {
        totalRevenue = BigDecimal.ZERO;
        totalOutstanding = BigDecimal.ZERO;

        if (allBills != null) {
            List<Bill> relevantBills = allBills.stream()
                    .filter(b -> b.getStatus() != BillStatus.VOIDED && b.getStatus() != BillStatus.DRAFT)
                    .collect(Collectors.toList());

            totalRevenue = relevantBills.stream()
                    .map(Bill::getTotalPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalOutstanding = relevantBills.stream()
                    .map(Bill::getBalanceDue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            paidBillsCount = relevantBills.stream().filter(b -> b.getStatus() == BillStatus.PAID).count();
            partiallyPaidBillsCount = relevantBills.stream().filter(b -> b.getStatus() == BillStatus.PARTIALLY_PAID).count();
            unpaidBillsCount = relevantBills.stream().filter(b -> b.getStatus() == BillStatus.FINALIZED).count();
        }
    }

    private void createFinancialSummaryBarChart() {
        financialSummaryBarChart = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Amount (Shs)");
        barDataSet.setBackgroundColor("rgba(75, 192, 192, 0.5)");
        barDataSet.setBorderColor("rgb(75, 192, 192)");
        barDataSet.setBorderWidth(1);

        List<Number> values = new ArrayList<>();
        values.add(totalRevenue);
        values.add(totalOutstanding);
        barDataSet.setData(values);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();
        labels.add("Total Revenue");
        labels.add("Total Outstanding");
        data.setLabels(labels);

        financialSummaryBarChart.setData(data);

        // Options
        BarChartOptions options = new BarChartOptions();
        options.setMaintainAspectRatio(false);
        CartesianScales scales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setBeginAtZero(true);
        scales.addYAxesData(linearAxes);
        options.setScales(scales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Financial Summary");
        options.setTitle(title);

        financialSummaryBarChart.setOptions(options);
    }

    private void createMonthlyRevenueLineChart(List<Bill> allBills) {
        monthlyRevenueLineChart = new LineChartModel();
        ChartData data = new ChartData();

        // Group payments by month
        Map<Month, BigDecimal> revenueByMonth = allBills.stream()
                .filter(b -> b.getStatus() == BillStatus.PAID || b.getStatus() == BillStatus.PARTIALLY_PAID)
                .flatMap(b -> b.getPayments().stream())
                .collect(Collectors.groupingBy(
                        payment -> payment.getPaymentDate().getMonth(),
                        Collectors.mapping(payment -> payment.getAmountPaid(), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        LineChartDataSet dataSet = new LineChartDataSet();
        dataSet.setLabel("Monthly Revenue (Shs)");
        dataSet.setFill(true);
        dataSet.setBackgroundColor("rgba(54, 162, 235, 0.2)");
        dataSet.setBorderColor("rgb(54, 162, 235)");
        dataSet.setTension(0.4);

        List<Object> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Populate with data for all 12 months, putting 0 for months with no revenue
        for (Month month : Month.values()) {
            labels.add(month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            values.add(revenueByMonth.getOrDefault(month, BigDecimal.ZERO));
        }

        dataSet.setData(values);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        monthlyRevenueLineChart.setData(data);

        // Options
        LineChartOptions options = new LineChartOptions();
        options.setMaintainAspectRatio(false);
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Revenue Trend by Month");
        options.setTitle(title);

        CartesianScales scales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setBeginAtZero(true);
        scales.addYAxesData(linearAxes);
        options.setScales(scales);

        monthlyRevenueLineChart.setOptions(options);
    }

    // Getters for stats
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public long getPaidBillsCount() { return paidBillsCount; }
    public long getPartiallyPaidBillsCount() { return partiallyPaidBillsCount; }
    public long getUnpaidBillsCount() { return unpaidBillsCount; }

    // Getters for charts
    public BarChartModel getFinancialSummaryBarChart() { return financialSummaryBarChart; }
    public LineChartModel getMonthlyRevenueLineChart() { return monthlyRevenueLineChart; }
}