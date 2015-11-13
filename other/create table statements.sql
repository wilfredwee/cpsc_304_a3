drop table itemPurchase;
drop table purchase;
drop table book;
drop table item;

create table item (
upc char(6),
sellingPrice float not null,
stock int not null,
taxable char(1) not null,
primary key(upc),
check(LOWER(taxable) IN ('y','n')),
check (stock>=0)
 );

create table book (
upc char(6),
title varchar(50) not null,
publisher varchar(50) not null,
flag_text char(1) not null,
primary key(upc),
foreign key(upc) references item,
check(LOWER(flag_text) IN ('y','n'))
 );

create table purchase (
t_id char(6),
purchaseDate date not null,
totalamt float not null,
pur_type varchar(15) not null,
cardno char(12),
cardtype varchar(12),
primary key(t_id),
check(LOWER(pur_type) IN ('cash','credit card')),
check(totalamt>=0)
 );

create table itemPurchase (
t_id char(6),
upc char(6),
quantity float not null,
primary key(t_id,upc),
foreign key(t_id) references purchase,
foreign key(upc) references item,
check(quantity>=0)
  );

