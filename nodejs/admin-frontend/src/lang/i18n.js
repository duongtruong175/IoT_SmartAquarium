import Vue from 'vue'
import VueI18n from 'vue-i18n'
import translateEn from './en'
import translateVi from './vi'

Vue.use(VueI18n);

export default new VueI18n({
    locale: 'en',
    silentTranslationWarn: true,
    messages: {
        en: translateEn,
        vi: translateVi,
    },
});
