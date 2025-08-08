#构建gLibc，playwright依赖2.27版本
RUN yum -y install glibc-devel && \
    yum -y update openssh util-linux bash-completion gnupg2 python perl systemd && \
    yum -y install alios7u-2_30-gcc-9-repo.noarch && \
    yum -y install libdb binutils && \
    yum -y install glibc glibc-langpack-en && \
    yum -y install glibc-devel libstdc++-devel libstdc++-static libstdc++ libasan && \
    yum clean all

#安装高版本的node，连带安装npm
RUN curl -s -L -o node.tar.gz https://npm.alibaba-inc.com/mirrors/node/v16.15.1/node-v16.15.1-linux-x64.tar.gz
RUN tar xf node.tar.gz -C /usr/local --strip-components=1
RUN rm node.tar.gz

#安装指定tnpm，tnpm的最新版9有问题，建议tnpm@latest-8 / latest-7 ,tnpm i tnpm@8 -g,tnpm install utf-8-validate@^5.0.2,tnpm install bufferutil@^4.0.1
RUN npm install --registry=https://registry.npm.alibaba-inc.com -g tnpm@latest-8

ENV ALINODE_VERSION 5.9.0
RUN curl -s -L -o node.tar.gz https://npm.alibaba-inc.com/mirrors/alinode/v$ALINODE_VERSION/alinode-v$ALINODE_VERSION-linux-x64.tar.gz
RUN tar xf node.tar.gz -C /usr/local --strip-components=1
RUN rm node.tar.gz

RUN tnpm install puppeteer@2
RUN tnpm install request@2
RUN tnpm install request-promise@4

# 安装 chrome
RUN wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
RUN yum install -y nss

RUN yum install -y pango.x86_64 libXcomposite.x86_64 libXcursor.x86_64 libXdamage.x86_64 libXext.x86_64 libXi.x86_64 libXtst.x86_64 cups-libs.x86_64 libXScrnSaver.x86_64 libXrandr.x86_64 GConf2.x86_64 alsa-lib.x86_64 atk.x86_64 gtk3.x86_64 ipa-gothic-fonts xorg-x11-fonts-100dpi xorg-x11-fonts-75dpi xorg-x11-utils xorg-x11-fonts-cyrillic xorg-x11-fonts-Type1 xorg-x11-fonts-misc
RUN yum install -y epel-release libappindicator
RUN yum install -y wqy-unibit-fonts.noarch wqy-zenhei-fonts.noarch

RUN wget https://dl.google.com/dl/linux/direct/google-chrome-stable_current_x86_64.rpm -O /home/admin/google-chrome-stable_current_x86_64.rpm
RUN { yum localinstall -y /home/admin/google-chrome-stable_current_x86_64.rpm || true; }
RUN yum install -y \
    libicu \
    libatomic \
    libxslt \
    woff2 \
    libevent \
    opus \
    libgcrypt \
    libjpeg-turbo \
    libpng \
    libwebp \
    enchant \
    libsecret \
    hyphen \
    pcre2 \
    psl \
    nghttp2 \
    gnutls \
    mesa-libGLES \
    x264
RUN yum clean all

ENV PLAYWRIGHT_DOWNLOAD_HOST=https://anpm.alibaba-inc.com/mirrors/playwright