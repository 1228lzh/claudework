/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        'crm-bg': '#FFFFFF',
        'crm-bg-secondary': '#F0F2F7',
        'crm-nav-bg': '#202230',
        'crm-primary': '#1F74FF',
        'crm-primary-light': '#EBF6FF',
        'crm-text-primary': '#1D222A',
        'crm-text-secondary': '#4E596A',
        'crm-text-tertiary': '#B3B6BF',
        'crm-text-light': 'rgba(255, 255, 255, 0.7)',
        'crm-border': '#4E596A',
      },
      fontSize: {
        'xs': '12px',
        'sm': '14px',
        'base': '16px',
      },
      lineHeight: {
        'tight': '20px',
        'normal-crm': '22px',
        'menu': '42px',
        'menu-sm': '40px',
      },
      spacing: {
        '1px': '1px',
        '69': '69.7188px',
      },
      borderRadius: {
        'crm': '2px',
      },
      boxShadow: {
        'crm-card': 'rgba(0, 0, 0, 0.03) 0px 12px 48px 16px, rgba(0, 0, 0, 0.05) 0px 9px 28px 0px, rgba(0, 0, 0, 0.08) 0px 6px 16px -8px',
        'crm-dropdown': 'rgba(0, 0, 0, 0.16) 0px 3px 12px 0px, rgba(0, 0, 0, 0.1) 0px 3px 1px 0px',
      },
      fontFamily: {
        'crm': ['-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', '"PingFang SC"', '"Hiragino Sans GB"', '"Microsoft YaHei"', '"Helvetica Neue"', 'Helvetica', 'Arial', 'sans-serif'],
        'crm-icon': ['iconfont-menu'],
      },
    },
  },
  plugins: [],
};
