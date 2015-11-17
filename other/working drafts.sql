create or replace view item_book as
  select i.upc
  from item i, book b
  where i.upc = b.upc


create or replace view book_itemPurchase as
  select ip.upc, ip.t_id, ip.quantity
  from itempurchase ip, item_book ib
  where ip.upc = ib.upc


create or replace view book_withinDateNQuantity as
  select bip.upc
  from book_itemPurchase bip, purchase p
  where bip.t_id = p.t_id
  and
  p.purchaseDate >= TO_DATE('2015-10-25', 'YYYY-MM-DD')
  and
  p.purchaseDate <= TO_DATE('2015-10-31', 'YYYY-MM-DD')
  group by bip.upc
  having sum(quantity) > 50

create or replace view bdq_isTextbook as
  select b.upc
  from book b, book_withinDateNQuantity bdq
  where b.upc = bdq.upc
  and b.flag_text = 'y'

create or replace view book_lessStock as
  select b.upc
  from book b, item i
  where b.upc = i.upc
  and
  i.stock < 10


select *
from book b
where b.upc in (
  select bls.upc
  from book_lessStock bls, bdq_isTextbook bdqt
  where bls.upc = bdqt.upc
)


create or replace view item_withinDate as
  select ip.upc, p.t_id
  from itemPurchase ip, purchase p
  where p.t_id = ip.t_id
  and
  p.purchaseDate >= TO_DATE('2015-10-25', 'YYYY-MM-DD')
  and
  p.purchaseDate <= TO_DATE('2015-10-31', 'YYYY-MM-DD')


create or replace view salessum_withindate as
  select ip.upc, sum(ip.quantity * i.sellingPrice) as salesSum
  from item i, itemPurchase ip
  where i.upc = ip.upc
  and ip.t_id in (
    select t_id
    from item_withinDate
    )
  group by ip.upc


select *
from item i
where i.upc in (
  select upc
  from (
    select *
    from salessum_withindate sswd
    order by sswd.salesSum desc
  )
  where rownum <= 3
)


select * from
  (select temp.upc, (temp.sumsold * i.sellingPrice) as totalsale
    from (
      select ip.upc, sum(ip.quantity) as sumsold
      from purchase p, itempurchase ip
      where p.t_id = ip.t_id AND p.purchaseDate >= TO_DATE('2015-10-25', 'YYYY-MM-DD') AND p.purchaseDate <= TO_DATE('2015-10-31', 'YYYY-MM-DD')
      group by ip.upc
      ) temp, item i
    where temp.upc = i.upc ORDER BY totalsale desc)
  where rownum <= 3



