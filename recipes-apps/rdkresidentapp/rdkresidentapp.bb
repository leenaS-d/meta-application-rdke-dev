DESCRIPTION = "Resident app implementation for RDK reference application"
HOMEPAGE = ""

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3cc4d276e918f48b04eb2faf952d0537"

RDEPENDS:${PN} += "bash lighttpd wpeframework"
inherit systemd syslog-ng-config-gen
SYSLOG-NG_FILTER = "residentapp"
SYSLOG-NG_SERVICE_residentapp = "residentapp.service"
SYSLOG-NG_DESTINATION_residentapp = "residentapp.log"
SYSLOG-NG_LOGRATE_residentapp = "low"

# FIXME: Move to a common config
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PACKAGE_ARCH = "${APP_LAYER_ARCH}"

S = "${WORKDIR}/git"

SRC_URI = "${CMF_GIT_ROOT}/rdk/components/generic/appmanager;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=generic"

# FIXME: Move to a common config
SRC_URI += "file://ref-webui-docroot-path.conf"

# Remove once RDKEMW-671 is release. Workaround to fix UI issue
SRC_URI += "file://wpeframework-rdkshell.service"

SRCREV_generic = "${AUTOREV}"
SRCREV_FORMAT = "generic"

do_install() {
   install -d ${D}${systemd_unitdir}/system
   install -m 0644 ${S}/resources/systemd/residentapp.service ${D}${systemd_unitdir}/system/residentapp.service

   sed -i '/^After/s/$/ wpeframework-rdkshell.service/' ${D}${systemd_unitdir}/system/residentapp.service
   sed -i '/^Requires/s/$/ wpeframework-rdkshell.service/' ${D}${systemd_unitdir}/system/residentapp.service

   install -d ${D}/lib/rdk
   install -m 0755 ${S}/residentapp/residentApp.sh ${D}/lib/rdk/residentApp.sh
}

# Remove once RDKEMW-671 is release. Workaround to fix UI issue
do_install:append() {
   install -m 0644 ${WORKDIR}/wpeframework-rdkshell.service ${D}${systemd_unitdir}/system/wpeframework-rdkshell.service
}

# FIXME: Move to a common config
do_install:append() {
    install -d ${D}${sysconfdir}/lighttpd.d
    install -m 0644 ${WORKDIR}/ref-webui-docroot-path.conf ${D}${sysconfdir}/lighttpd.d/
}

SYSTEMD_SERVICE:${PN} = "residentapp.service"
FILES:${PN} += "${systemd_unitdir}/system/residentapp.service"
FILES:${PN} += "/lib/rdk/residentApp.sh"

# Remove once RDKEMW-671 is release. Workaround to fix UI issue
SYSTEMD_SERVICE:${PN} += "wpeframework-rdkshell.service"
FILES:${PN} += "${systemd_unitdir}/system/wpeframework-rdkshell.service"

