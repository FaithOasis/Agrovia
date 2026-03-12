// Central mock data used by multiple dashboard components
export const menu = [
  {
    id: 1,
    title: "Main",
    listItems: [
      { id: 1, url: "/", icon: "/icons/home.svg", title: "Home" },
      { id: 2, url: "/dashboard", icon: "/icons/chart.svg", title: "Dashboard" },
    ],
  },
  {
    id: 2,
    title: "Manage",
    listItems: [
      { id: 3, url: "/products", icon: "/icons/package.svg", title: "Products" },
      { id: 4, url: "/orders", icon: "/icons/orders.svg", title: "Orders" },
    ],
  },
];

export const topDealUsers = [
  { id: 1, img: "/avatars/avatar1.png", username: "Alice", email: "alice@example.com", amount: 420 },
  { id: 2, img: "/avatars/avatar2.png", username: "Bob", email: "bob@example.com", amount: 370 },
  { id: 3, img: "/avatars/avatar3.png", username: "Charlie", email: "charlie@example.com", amount: 310 },
];

// ChartBox props (shape expected by ChartBox component)
const sampleLineData = [
  { name: "Week 1", value: 400 },
  { name: "Week 2", value: 300 },
  { name: "Week 3", value: 500 },
  { name: "Week 4", value: 450 },
];

export const chartBoxUser = {
  color: "#10b981",
  icon: "/icons/user.svg",
  title: "Users",
  dataKey: "value",
  number: 1240,
  percentage: 12,
  chartData: sampleLineData,
};

export const chartBoxProduct = {
  color: "#3b82f6",
  icon: "/icons/product.svg",
  title: "Products",
  dataKey: "value",
  number: 540,
  percentage: 6,
  chartData: sampleLineData,
};

export const chartBoxConversion = {
  color: "#f59e0b",
  icon: "/icons/convert.svg",
  title: "Conversion",
  dataKey: "value",
  number: "4.2%",
  percentage: -1.4,
  chartData: sampleLineData,
};

export const chartBoxRevenue = {
  color: "#ef4444",
  icon: "/icons/revenue.svg",
  title: "Revenue",
  dataKey: "value",
  number: "$12.4k",
  percentage: 3.2,
  chartData: sampleLineData,
};

export const barChartBoxVisit = {
  title: "Visits",
  color: "#06b6d4",
  dataKey: "visits",
  chartData: [
    { name: "Mon", visits: 120 },
    { name: "Tue", visits: 200 },
    { name: "Wed", visits: 150 },
    { name: "Thu", visits: 220 },
    { name: "Fri", visits: 170 },
  ],
};

export const barChartBoxRevenue = {
  title: "Sales",
  color: "#8b5cf6",
  dataKey: "sales",
  chartData: [
    { name: "Mon", sales: 400 },
    { name: "Tue", sales: 300 },
    { name: "Wed", sales: 500 },
    { name: "Thu", sales: 450 },
    { name: "Fri", sales: 600 },
  ],
};

export default {};
